#!/usr/bin/env python2.6
#Cuijun Wu cuijunwu
#John Keesling	
#Mami Hackl mami1203
#D4 assemble summaries; post processing

import sys,re,os,nltk

#output directory path
path = sys.argv[1]

### define regex for sentence simplification
#initial adverbials 
init_adv = re.compile('((But|Thus|And|However|In conclusion|For example|On the other hand|As a matter of fact|At this point|Therefore|Also|also|elsewhere|As a result|Still|In addition|So far|Clearly|Moreover|Last year|Elsewhere|In any case|Nonetheless|Instead|In the same issue|Overall|First|After all|In fact|Meanwhile|Now)\,? )')
header = re.compile('^((\s*\w+){1,2})?(\s*\(\w+\))?\,?((\s*\S+){1,2})?( \(\w+\))?\s?[-_]{1,2} ')

#attribution clauses
#adv1 = re.compile('(\,?[\w\s]+ (said|says|noted)\,?\s?([A-Z]{1}\w+)?)')
adv1 = re.compile('(\,?(\s*\S+){1,5} (said|says|noted)[,]?(( on)?( \w+day))?( that)?)')

#grammar = r"APP: {<,><.*>+<,>}"

#appositives
grammar = r"""
  APP:{<,><.*>+<,>}	               # Chunk everything
   }<,><VBP|VBN|VBZ|VBD><.*>*<,>{      # Chink sequences of VBD and IN
  """

#appositives
#grammar = r"""
#  APP:{<,><.*>+<,>}                   # Chunk everything
#   }(<,><.*>*<VBP|VBN|VBZ><.*>*<,>){  # Chink sequences
#  """


#grammar2 = r"APP:{<,><VB>+<,.>}"    # Chunk everything

# get files in output directory
file_list = os.listdir(path)
for file in file_list:
     
    filepath = os.path.join(path,file)
    # if it's directory skip
    if os.path.isdir(filepath):
       continue

    length = 0
    list = []
    NE_dict = []
    APP_dict = []

    # it's a file so process
    input = open(filepath,'r')
    outfile = re.sub('(.*).txt','\g<1>',filepath)
    output = open(outfile,'w')

    #debuggin purpose
    #if re.search('(.*).txt',file):
       #debugfile = re.sub('(.*).txt','\g<1>',file)
       #debug = open('./debug/' + debugfile + '_debug','w')

    for s in input.readlines():

       #remove initial adverbials
       k = init_adv.search(s)
       if k:
          s = re.sub(k.group(1),'',s)
          #debug.write('init_adv: ' + str(k.group(0)) + '\n')

       #remove header
       k = header.search(s)
       if k:
          s = s.replace(k.group(0),"")
          #debug.write('head: ' + str(k.group(0)) + '\n')

       #remove garbage
       k = re.search('(^[)_]+\s+|^.*\s{3,})',s)
       if k:
          s = s.replace(k.group(0),"")

       k = re.search('(^\w{1,3}[:.]\s+|[\'\`]{2})',s)
       if k:
          #debug.write('gar2 :' + str(k.group(0)) + '\n')
          s = re.sub(k.group(0),'',s)
       k = re.search('\s*(\(.*\))\s*',s)
       if k:
          #debug.write('gar3 :' + str(k.group(0)) + '\n')
          s = s.replace(k.group(0),"")

       #k = re.search('[\'\`]{1,2}(?!s)',s)
       #if k:
       #   s = re.sub(k.group(0),'',s)
       #   debug.write(str(k.group(0)) + '\n')

       # skip quoted sentences
       if re.search('(^\s*[\'\`]{2}|phone: |Wh\w+|How |^(\.\s?){2,3})',s):
         continue
       else:
        s1 = nltk.word_tokenize(s)
        s2 = nltk.pos_tag(s1)
        #debug.write(str(s2))
        # collect named entities
        s3 = str(nltk.ne_chunk(s2,binary=True))
        #debug.write(s3)
        # collect appositives 
        cp = nltk.RegexpParser(grammar)
        tree = cp.parse(s2)
        for subtree in tree.subtrees():
            if subtree.node == 'APP': 
                 temp = str(subtree)
                 #debug.write(temp + '\n')
                 temp = re.sub('\(APP[\n\s]','',temp) 
                 temp = re.sub('\/(?P<tag>[,:A-Z$]+)\)?\n?','',temp) 
                 temp = re.sub('  ', ' ',temp) 
                 temp = re.sub(' , ', ', ',temp) 
                 temp = re.sub('(\S+) ,', '\g<1>,',temp) 
                 temp = re.sub('(\w+) (\'s)', '\g<1>\g<2>',temp) 
                 if not re.search('.*\,$',temp): 
                    temp = re.sub('(.*)', '\g<1>,',temp) 
                 k = re.search(r',\s.*,',temp)
                 if k:
                    APP_dict.append(str(k.group(0)))
                 #if not re.search(r'[\'):]+',temp): 
                 #   APP_dict.append(str(k.group(0)))
                    #debug.write('add app_list: ' + str(k.group(0)) + '\n')

        lines = re.split('\n',s3)
        for l in lines:
           # i.e., (NE Celebrex/NNP)
           m = re.findall('\(NE (.*)\)',l) 
           if m:
             for ne in m:
               n = re.split(' ',ne)
               if n:
                 for token in n:
                    k = re.search('(.*)\/[A-Z]+\)?',token) 
                    if k:
                       #debug.write(str(k.group(1)) + ' ')
                       NE_dict.append(k.group(1))

        #remove appositives 
        if len(APP_dict) > 0:
          for app in APP_dict:
            k = re.search(app,s)
            if k:
               #debug.write(str(s) + '\n')
               s = re.sub(app,'',s) 
               #debug.write('app: ' + str(app) +  str(s)  +  '\n')

        #remove header
        #k = header.search(s)
        #if k:
        #   s = s.replace(k.group(0),"")
        #   debug.write('head: ' + str(k.group(0)) + '\n')

        #remove attribution clauses
        k = adv1.search(s)
        if k:
           s = re.sub(k.group(0),'',s)
           #debug.write('adv1: ' + str(k.group(0)) + '\n')
        # remove space
        k = re.match('^\s+',s)
        if k:
           s = re.sub(k.group(0),'',s,1)

        gar1 = re.compile("\s*&(\S+)?;\s*")
        #collect 250 words
        tokens = re.split(' ',s)
        counter = 0
        tempLen = 0 
        tempL = []
        for entry in tokens:
          k = gar1.match(entry)
          if k:
            entry = re.sub(k.group(0),'',entry)
          if re.match('\S',entry):
             #capitalize the first word in the sentence
             if counter == 0:
                if not entry.isupper():
                  if not entry.istitle():
                     entry = entry.capitalize()
             tempL.append(entry)
             counter += 1

        #setence too short, move on
        if counter < 7:
           continue

        # check current length
        tempLen = length + counter
        # case1. length overflow, move on
        if tempLen > 250:
          del tempL[:]
          tempL[:] = []
          continue
        # case2. length just right
        elif tempLen > 240 and tempLen <= 250:
          list.extend(tempL)
          break
        # case3. length still smaller than 240, continue
        else:
          length = tempLen 
          list.extend(tempL)
          continue 
 
    #print out summary
    for entry in list:   
        output.write(entry)
        if not re.search('\n',entry): 
           output.write(' ')
    
# close file handlers
input.close()
output.close()
#debug.close()

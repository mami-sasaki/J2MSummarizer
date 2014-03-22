#!/usr/bin/env python2.6
#Cuijun Wu cuijunwu
#John Keesling
#Mami Hackl mami1203
#D4 assemble summaries; post processing

import sys,re,os,nltk

# file path
filepath = str(sys.argv[1])

# sentence
s = str(sys.argv[2])
# sentence file
sfile = open('sfile','w')
sfile.write(s)

# current directory path 
curpath = os.path.abspath(os.getcwd()) 

# anaphora output file 
anaph = open('anaph','w')

s1 = nltk.word_tokenize(s)
word_pair = str(nltk.pos_tag(s1))
word_pair = word_pair.replace('[', "")
word_pair = word_pair.replace('(', "")
word_pair = word_pair.replace(')', "")
word_pair = word_pair.replace(']', "")
word_pair = word_pair.replace('\'', "")
word_pair = word_pair.replace(',', "")
word_pair = word_pair.replace('.', "")
word_pairs = word_pair.split()
dict = {}
prev = ""

for tag in word_pairs:
    if re.match('^PRP*',tag):
       if prev not in dict:
          anaph.write(prev + '\n')
          dict[prev] = 1
    prev = tag 

anaph.close()
sfile.close()

# if the sentence doesn't have return, add one 
if not re.search('.*\n$',s):
   s += '\n'

# call core anaphora resolution module
command = './coref.sh ' + str(filepath) + ' sfile anaph ' + curpath 

#out = open('anaphora.out','w')
l = os.popen(command)
for m in l.readlines():
    sys.stdout.write(m)
#    out.write(m)

#out.close()

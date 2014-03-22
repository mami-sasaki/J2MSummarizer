#!/usr/bin/env python2.6
#Cuijun Wu cuijunwu
#Mami Hackl mami1203
#John Keesling	

import sys,re,os

#open files
input = open(sys.argv[1],'r')
data = input.read()
#input = open(sys.argv[2],'r')
output = open('output','w')

spat = re.compile('[.?][\s\n]+')
qpat = re.compile('([A-Z0-9]+): (.*\n)')
type1 = re.compile('(Wh|wh.*\?) ')
type2 = re.compile('(How|how.*\?) ')
type3 = re.compile('Include(.*)')
#type4 = re.compile('(Discuss|Describe|Note|Specify|Track)(.*)')

# parse topic file and categorize queries 
m = qpat.findall(data)
dict = {}
for pair in m:
    tnum = pair[0]    
    query = pair[1]
    l = spat.split(query)
    if l:
         for s in l:
          output.write(s + '\n')
          n = type1.findall(s) 
          if n:
            for q in n:
                 output.write(q + '\n')
          n = type2.findall(s)       
          if n:
             for q in n:
                 output.write(q + '\n')
          n = type3.search(s)       
          if n:
             #if type3.search(e):   
             output.write("key words: " + n.group(1) + '\n')
 
#close file handlers
input.close()
#input.close()
output.close()

 

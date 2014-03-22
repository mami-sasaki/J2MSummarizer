#!/usr/bin/env python2.6

import sys,re,os,nltk

file = open("../output/orgQ.txt", "r")
file2 = open('../output/taggedQ.txt','w')
text = file.read()
text = nltk.word_tokenize(text)

word_pair = str(nltk.pos_tag(text))
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
for word in word_pairs:
	#if word == 'NN' or word == 'NNP' or word == 'NNS':
	if re.match('^NN',word):
		if prev not in dict:
                        file2.write(prev + " ")
			dict[prev] = 1;
	prev = word
file2.write("\n")

file.close()
file2.close()

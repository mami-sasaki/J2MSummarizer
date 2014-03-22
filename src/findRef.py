import sys

##Usage: [1] == file to be processed
##       [2] == a file containing the original sentence in question
##       [3] == a file containing the original anaphor being resolved
## outputs the sentence with the anaphor replaced by its referent 

f = open(sys.argv[1]).readlines()

orig_sent = open(sys.argv[2]).readlines()[0]

anaphor = open(sys.argv[3]).readlines()[0].strip('\n')

named_ents = {}

n = 0

finish_ne = False
new_line = False
lastID = 0

#build named_ents dictionary
for line in f:
	wds = line.split(' ')
	for i in range(0, len(wds)-1):
		wd = wds[i].strip('\n')
		if finish_ne:
			if wd not in named_ents[lastID]:
				new = named_ents[lastID] + ' ' + wd
				named_ents[lastID] = new
			c = 0
			for letter in named_ents[lastID]:
				if letter == '>':
					c += 1
			if c >= 2:
				finish_ne = False
		elif 'ID=' in wd:
			id = int(wd.split('=')[1].strip('"'))
			lastID = id
			next_word = wds[i+1].rstrip(',').rstrip(')')
			if id not in named_ents:
				named_ents[id] = next_word
				count = 0
				for letter in next_word:
					if letter == '>':
						count += 1
				if count < 2:
					finish_ne = True
	new_line = True

if anaphor == 'his' or anaphor == 'her' or anaphor == 'its':
	possessive = True
else:
	possessive = False

#find sentence in file
orig_wds = orig_sent.split(' ')
anaphor_index = 0
for i in range(0, len(orig_wds)-1):
	cur = orig_wds[i]
	if cur == anaphor:
		anaphor_index = i
		break

first_chunk = orig_wds[0:anaphor_index]
first_chunk_string = ''
for w in first_chunk:
	first_chunk_string = first_chunk_string + w + ' '
second_chunk = orig_wds[anaphor_index+1:len(orig_wds)]
second_chunk_string = ''
for w in second_chunk:
	second_chunk_string = second_chunk_string + w + ' '
	
important_line = ''
similarity = 0
current_line = (0, '')
for line in f:
	line = line.strip('</s>')
	similarity = 0
	wds = line.split(' ')
	for wd in orig_wds:
		if wd in wds:
			similarity += 1
	if similarity > current_line[0]:
		current_line = (similarity, line)

important_wds = current_line[1].split(' ')
real_ent = ''
for i in range(0, len(important_wds)-1):
	wd = important_wds[i]
	if 'ID=' not in wd:
		continue
	else:
		curID = int(wd.split('=')[1].strip('"'))
		next_wd = important_wds[i+1]
		if anaphor in next_wd:
			real_ent = named_ents[curID]
			real_ent = real_ent.split('>')[1]
			real_ent = real_ent.split('<')[0]
			thing = real_ent.split(';')
			real_ent = thing[len(thing)-1]
			real_ent = real_ent.strip()
			
for wd in orig_sent.split(' '):
	if wd == anaphor:
		sys.stdout.write(real_ent)
		if possessive:
			sys.stdout.write("'s ")
		else:
			sys.stdout.write(' ')
	else:
		sys.stdout.write(wd + ' ')

#for id in named_ents:
#	print str(id) + ' ' + named_ents[id]

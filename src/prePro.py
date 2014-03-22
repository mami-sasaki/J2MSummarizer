import sys

f = open(sys.argv[1]).readlines()

for line in f:
	line = line.strip('\n')
	line = line.rstrip(' ')
	wds = line.split(' ')
	line = line.replace('</s>', '\n')
	last_wd = wds[len(wds)-1]
	if line != '':
		sys.stdout.write(line)
	if '.' in last_wd:
		sys.stdout.write('\n')
	else:
		sys.stdout.write(' ')

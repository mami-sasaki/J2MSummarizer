#!/usr/bin/perl -w
use strict;

open(my $input, $ARGV[0]) || die "$ARGV[0] does not exist\n";
open(my $output, '>'.$ARGV[1]) || die "$ARGV[1] does not exist\n";
my @sent = ();
my $index = 0;
my %all_sent = ();

# put all the sentences into an array
while(<$input>){
	chomp;
	$sent[$index] = $_;
	$all_sent{$_} = 1;
	$index++;
}

my $cnt = 0;
my @similar = ();

# outer loop to go through all the sentences
for(my $i = 0; $i < $index; $i++){
	my %line = ();
	my @words = split(/\s+/, $sent[$i]);
	for(my $j = 0; $j < scalar @words; $j++){
		$line{$words[$j]} = 1;
	}
	# inner loop to go through the rest of the sentences
	for(my $k = $i + 1; $k < $index; $k++){
		my $score = 0;
		my @curr_words = split(/\s+/, $sent[$k]);
		for(my $y = 0; $y < scalar @curr_words; $y++){
			$score++ if(exists($line{$curr_words[$y]}));
		}
		# check if the sentences are similar
		my $len = scalar @curr_words;
		if($score/$len > 0.80){
			$similar[$cnt] = $sent[$i];
			$cnt++;
		}
	}
}

for(my $j = 0; $j < scalar @similar; $j++){
	$all_sent{$similar[$j]} = 0; 
}

# output non-overly-similar sentences
for(my $i = 0; $i < $index; $i++){
	print $output $sent[$i]."\n" if($all_sent{$sent[$i]} == 1);
}

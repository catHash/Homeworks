#!/bin/bash
#catch --test option to not delete anything
S1='--test'
test=0
if [ $# -lt 2 ]; then 
	echo "illegal number of parameters"
	echo "SYNTAX: dirclean [--test] ROOT_DIRECTORY PATTERN..."
	exit 1
fi
if [ "${1,,}" = "$S1" ]; then
	echo "TEST OPTION SUPPLIED"
	test=1
	shift
fi
if [ -d "$1" ]; then
	cd "$1"
else
	echo "ERROR: arg1 was not a dir..."
	echo "SYNTAX: dirclean [--test] ROOT_DIRECTORY PATTERN..."
	exit 1
fi
shift
#maybe this should be for a in $ROOT_DIRECTORY
for a in *; do
	echo "a"
	echo "$a"
	#loop through command line args
	for e in "$@"; do
		echo "e"
		echo "$e"
		if [ "$a" = "$e" ]; then
			echo "Deleting File: $e"
			echo "$test"
			if [ ! $test ]; then
				rm "$e"
			fi
		fi
	done
done
exit 0

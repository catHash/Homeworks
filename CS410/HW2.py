#!/usr/bin/env python
#
#	Author: Brian Gunn
#	Southern Illinois University
#	CS 410 Homework #2
#	Hash script using BitVector
#	version 1.0
#
import BitVector
import os
import sys
bv = BitVector.BitVector( intVal = 0, size = 32 )
bv2 = BitVector.BitVector( size = 8 )
#file = open(sys.argv[1])
f = open('HashOutput.txt','w')
fileList = os.listdir('.')
for i in fileList:
	if os.path.isfile(i): 
		bv.reset(0)
		bv1 = BitVector.BitVector(filename = i)
		while True:
    			bv2 = bv1.read_bits_from_file(8)
   			if not bv2:
				#end of file
        			break
    			#print("Read a byte:",bv2)
    			bv[0:8] = bv[0:8] ^ bv2
   			bv << 8
		f.write(i)
		f.write(' ')
		f.write(str(hex(int(bv))))
		f.write('\n')

import BitVector
import glob   

path = 'd:/test/*.txt'   
files=glob.glob(path)   

hash = BitVector.BitVector(size = 32)
hash.reset( 0 )
print(hash)
x = 0
for file in files:
    bv = BitVector.BitVector( filename = file)
    while 1 :
        bv1 = bv.read_bits_from_file(8)
        if str(bv1)=="":
            break
        hash[0:8] = bv1 ^ hash[0:8]
        hash >> 8
        x = x + 1
hash_str = ""
hash_str = str( hash )
text_file = open("d:/test/res.txt", "w")
text_file.write("Hash Code: %s" %hash_str)
text_file.close()

print (hash)
print (x)


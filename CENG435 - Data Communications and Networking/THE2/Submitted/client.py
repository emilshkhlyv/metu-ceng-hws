import sys
import socket
import time
import hashlib
import select

# Get arguments from command line
serverIpAddress = sys.argv[1]
serverUDPPort = int(sys.argv[2])
serverTCPPort = int(sys.argv[3])
senderUDPPort = int(sys.argv[4])
senderTCPPort = int(sys.argv[5])

# Create TCP Socket
TCPSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Bind to sender TCP Port
TCPSocket.bind(('', senderTCPPort))

# Connect to server TCP Port
TCPSocket.connect((serverIpAddress, serverTCPPort))

# File path of file which will be sent by TCP
TCPPath = "./transfer_file_TCP.txt"

# Open File
TCPFile = open(TCPPath, 'r')

# Firstly read 970 bytes from File
TCPText = TCPFile.read(970)

# Do until the end of file
while TCPText != "":

    # Starting time before send packet to server
    start = time.time()

    # Send time information and 970 bytes from TCP File to server
    TCPSocket.send((f'{start:<30}' + TCPText).encode())

    # Again read 970 bytes from File
    TCPText = TCPFile.read(970)

# Close TCP File
TCPFile.close()

# Close TCP Socket
TCPSocket.close()


"""UDP PART"""
# Create UDP Socket
UDPSocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

# Bind to sender UDP Port
UDPSocket.bind(('', senderUDPPort))

# Connect to server UDP Port
UDPSocket.connect((serverIpAddress, serverUDPPort))

# File path of file which will be sent by UDP
UDPPath = "./transfer_file_UDP.txt"

# Open File
UDPFile = open(UDPPath, 'r')

# Variable for keeping number of packets
i = 0

# Array for keeping packets
paketler = []

# Creating packets
while True:
    # Read 928 character each time and put them to packet and packets array
    text = UDPFile.read(928)
    if not text:
        break
    package = f'{i:<10}' + text
    i += 1
    paketler.append(package)

# Total Count of packets
totalPacketCount = len(paketler)

# Next packet which will be sent
nextPacket = 0

# Starting of the window which shows the first element of Window
startingOfWindow = 0

# Windows size normally fixed to 5, but as implementation based it can be decreased
windowSize = min(totalPacketCount - startingOfWindow, 5)

# Variable for count how many packets resend
resend = 0

# Do sending till end of the packets array
while startingOfWindow < totalPacketCount:
    # variable for calculating is timeout happened or not
    timeout = time.time()

    # If the packet which wanted to be sent to server is can be locate in window, so send it
    while nextPacket < startingOfWindow + windowSize:
        start = time.time()
        msg = f'{start:<30}' + paketler[nextPacket]
        md5 = hashlib.md5(msg.encode()).hexdigest()
        UDPSocket.send((f'{md5:<32}' + msg).encode())
        nextPacket += 1

    # I used select module for getting input from server with timeout feature
    UDPSocket.setblocking(0)

    # If the timeout happened while waiting for message from server,
    # or the message is received from server,
    # do necessary calculations
    Halo = select.select([UDPSocket], [], [], 1)
    if Halo[0]:
        response = UDPSocket.recv(20)
        num = int(response[4:])
        # if acknowledged packet is in window, increase starting of window
        if num >= startingOfWindow and time.time() - timeout < 1:
            startingOfWindow = num + 1
            windowSize = min(totalPacketCount - startingOfWindow, 5)
    # Timeout happened resend packets
    if time.time() - timeout >= 1:
        resend += windowSize
        nextPacket = startingOfWindow
# Send message for informing it is over with file
UDPSocket.send(''.encode())
# Send total count of resended packets
UDPSocket.send(str(resend).encode())

# Close socket and File
UDPSocket.close()
UDPFile.close()

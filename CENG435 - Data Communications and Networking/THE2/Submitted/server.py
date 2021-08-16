import sys
import socket
import time
import hashlib

# Get arguments from command line
serverUDPPort = int(sys.argv[1])
serverTCPPort = int(sys.argv[2])

# File path of file which the transmitted file will be written inside
TCPPath = "./transfer_file_TCP.txt"

# Open File
TCPFile = open(TCPPath, 'w')

# Create TCP socket
serverTCPSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Bind to server TCP Port
serverTCPSocket.bind(('', serverTCPPort))

# Start listen as Server
serverTCPSocket.listen(1)

# Average Transmission Time
ATT = 0

# Count of Packages
loop = 1

# First timestamp sent from client
first = 0

# Flag for getting first timestamp which sent from client
FirstStartFlag = False

# Establish connection
clientSocket, clientAddress = serverTCPSocket.accept()

with clientSocket:
    while True:

        # Get message from client
        msg = clientSocket.recv(1000)

        # if there's no message quit loop
        if not msg:
            break

        # stop the time after the packet is received
        stop = time.time()

        # timestamp for getting time when the message sent from client
        startTime = float(msg[:30])

        # Average Transmission Time
        ATT += stop - startTime

        # Count packet
        loop += 1

        # Get first timestamp
        if not FirstStartFlag:
            first = startTime
            FirstStartFlag = True

        # Write packet to TCP File
        TCPFile.write(msg.decode("utf-8")[30:])

    # Print information
    print(f'TCP Packets Average Transmission Time: {ATT * 1000 / loop} ms')
    print(f'TCP Communication Total Transmission Time: {(stop - first) * 1000} ms')

# Close TCP File
TCPFile.close()

# Close TCP socket
serverTCPSocket.close()


"""UDP PART"""
# File path of file which the transmitted file will be written inside
UDPPath = "./transfer_file_UDP.txt"

# Open File
UDPFile = open(UDPPath, 'w')

# Create UDP Socket
serverUDPSocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

# Bind to server UDP Port
serverUDPSocket.bind(('', serverUDPPort))

# Flag for getting first time from first packet
totalStartTimeFlag = False

# Variable for keeping starting time of sending from client Side
firstTime = 0

# Variable for calculating Average Transmission Time
ATT = 0

# Variable for total count of packets
k = 0

# Which Packet should be received
expectedNumber = 0

# Do till the empty message
while True:
    # Get message content and client address
    msg, addr = serverUDPSocket.recvfrom(1000)

    # If message is empty exit loop
    if not msg:
        break

    # Time of receiving packet from client
    stopTime = time.time()

    # calculate md5 hashcode in server side
    md5here = hashlib.md5(msg[32:]).hexdigest()

    # md5 hashcode which sent from client
    md5came = msg.decode()[:32]

    # Time of message is sent from client
    startTime = float(msg[32:62])

    # If it is the first message received from Client, get starting time
    if not totalStartTimeFlag:
        firstTime = startTime
        totalStartTimeFlag = True

    # Get packet number from message
    packetNumber = int(msg[62:72])

    # Get content of message
    Text = msg.decode("utf-8")[72:]

    # If checksum failed send Corruption message and get right file
    if md5here != md5came:
        response = "COR " + str(expectedNumber - 1)
        serverUDPSocket.sendto(response.encode(), addr)
    # If the received packet is the packet should be come send acknowledgement to client and write to file
    elif packetNumber == expectedNumber:
        response = "ACK " + str(expectedNumber)
        serverUDPSocket.sendto(response.encode(), addr)
        ATT += stopTime - startTime

        # count packets
        k += 1

        # increase expectedNumber Variable
        expectedNumber += 1

        # Write to File
        UDPFile.write(Text)
    # If it is not the packet should be come send acknowledgement to client about it
    else:
        # send the acknowledgement about the last received packet
        response = "ACK " + str(expectedNumber-1)
        serverUDPSocket.sendto(response.encode(), addr)

# Get total resend count
resend_count = serverUDPSocket.recv(10)

# Print necessary information
print(f'UDP Packets Average Transmission Time: {ATT * 1000 / k} ms')
print(f'UDP Communication Total Transmission Time: {(stopTime - firstTime) * 1000} ms')
print(f'UDP Transmission Re-transferred Packets: {int(resend_count)}')

# Close socket and File
serverUDPSocket.close()
UDPFile.close()

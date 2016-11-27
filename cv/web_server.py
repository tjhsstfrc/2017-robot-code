import socket

host = ''
port = 8080

c = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

c.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1) 

c.bind((host, port))

c.listen(1) 

while 1:  
    csock, caddr = c.accept()  
    cfile = csock.makefile('rw', 0)
    line = cfile.readline().strip()


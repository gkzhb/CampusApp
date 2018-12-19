from twisted.internet.protocol import Factory
from twisted.protocols.basic import LineReceiver
from twisted.internet import reactor
from sys import stdout
import mysql.connector
import json

needPara = "Insufficient parameters"

mydb = mysql.connector.connect(
	host="localhost",
	user="root",
	password="Zhb19981208",
	auth_plugin="mysql_native_password",
	database="test"
)
mycursor = mydb.cursor()


def logout(dt):
    print("logout")
    # 中断连接
    # ret["stat"] = 0
    ret = {"stat": 0}
    return ret


# login 登录
# 参数:
# 返回值:
# 0: 登录成功
# 1: 密码错误
# 2: User ID 不存在 
def login(dt):
    print("login")
    if (not "id" in dt) or (not "passwd" in dt):
        ret = {"stat": 1, "err": needPara}
        return ret
    usrID = dt["id"]
    passwd = dt["passwd"]
    # id = str(id)
    # passwd = str(passwd)
    sql = "SELECT usr_passwd FROM UsrInfo WHERE usr_usrid = '%s'" % (usrID, )
    mycursor.execute(sql)
    myresult = mycursor.fetchone()
    err = ""
    if not myresult:
        stat = 2  # print("User ID doesn't exist")
        err = "User ID doesn't exist"
    else:
        if passwd == myresult[0]:
            stat = 0  # print("Login successfully")
        else:
            stat = 1  # print("Wrong password")
            err = "Wrong password"
    ret = {"stat": stat}
    if stat != 0:
       ret["err"] = err
    return ret


def send_location():
    print("send location")


def chat():
    print("chat")


def add_friend():
    print("add friend")

# stat:
# 2: 用户名已存在
def sign_up(dt):
    print("sign up")
    if not "id" in dt or not passwd in dt or not name in dt:
        ret = {"stat": 1, "err": needPara}
    usrID = dt["id"]
    # id = str(id)
    passwd = dt["passwd"]
    # passwd = str(passwd)
    name = dt["name"]
    # name = str(name)
    sql = "SELECT usr_usrid FROM UsrInfo WHERE usr_usrid = '%s'" % (name, )
    # val = (name, )
    mycursor.execute(sql)
    myresult = mycursor.fetchone()
    if not myresult:
        sql = "INSERT INTO UsrInfo (usr_usrid, usr_passwd, usr_name) VALUES (%s, %s, %s)"
        val = (usrID, passwd, name)
        mycursor.execute(sql, val)
        print("affected rows = {}".format(mycursor.rowcount))
        mydb.commit()
        stat = 0  # print("Sign up successfully!")
    else:
        stat = 2  # print("User ID exists")
        err = "User ID exists"
    ret = {"stat": stat}
    if stat != 0:
        ret["err"] = err
    return ret


functions ={
	0: logout,
	1: login,
	2: sign_up,
	3: chat,
	4: send_location,
        5: add_friend
	}


class Chat(LineReceiver):

    def __init__(self, users):
        self.users = users
        self.name = None
        # self.state = "GETNAME"

    def connectionMade(self):
        print("Connection Made")
        # self.sendLine("What's your name?")

    def connectionLost(self, reason):
        print("lost")

    #def rawDataReceived(self,
    def lineReceived(self, line):
        line = line.decode(encoding="utf-8", errors="strict")
        print(line)
        recJson = json.loads(line)
        if not "instr" in recJson:
            ret = {"stat": 1, "err": needPara}
        else:
            x = recJson["instr"]
            if 0 <= x and x < 6:
                ret = functions[x](recJson)
            else:
                ret = {"stat": -1, "err": "No such operation!"}
        retJson = json.dumps(ret)
        self.sendLine(retJson.encode(encoding="utf-8"))
        # if self.state == "GETNAME":
            # self.handle_GETNAME(line)
        # else:
            # self.handle_CHAT(line)

    def handle_GETNAME(self, name):
        if name in self.users:
            self.sendLine("Name taken, please choose another.")
            return
        self.sendLine("Welcome, %s!" % (name))
        self.name = name
        self.users[name] = self
        self.state = "CHAT"

    def handle_CHAT(self, message):
        # message = "<%s> %s" % (self.name, message)
        message_list = message.split()
        if len(message_list) >= 1 and int(message_list[0]) < len(functions):
            functions[int(message_list[0])]()
        print(message)
        self.sendLine(message)
        # for name, protocol in self.users.iteritems():
        #    if protocol != self:
        #        protocol.sendLine(message)


class ChatFactory(Factory):

    def __init__(self):
        self.users = {}  # maps user names to Chat instances

    def buildProtocol(self, addr):
        return Chat(self.users)


reactor.listenTCP(8123, ChatFactory())
reactor.run()

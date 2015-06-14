#encoding:utf-8
import urllib2
import cookielib
import re
import random
import time
#sjwtu刷形势政策脚本
#Author	jtu唐
#QQ		461881920
#邮箱	jtutang@163.com
class swjtu_xszc():
	#登录请求链接
	login_url 	= "http://ocw.swjtu.edu.cn/servlet/UserLoginDataAction"
	#检测登录状态的链接
	check_url 	= "http://ocw.swjtu.edu.cn/websys/studentstudytime.jsp"
	#视频列表链接
	list_url 	= "http://ocw.swjtu.edu.cn/page/viewVideo.jsp?c_id=196"
	def __init__(self,username,password):
		self.username = username
		self.password = password

	#登录函数
	#登录成功返回True,否则返回False
	def login(self):
		param = "UserName=%s&Password=%s&UserType=stu" % (self.username,self.password)
		self.cj = cookielib.CookieJar()
		self.opener = urllib2.build_opener(urllib2.HTTPCookieProcessor(self.cj))
		res = self.opener.open(self.login_url,param).read()
		return "登录成功" in res
	
	#抓取视频链接函数
	#初始化后应首先运行此函数
	def grap_list(self):
		info = urllib2.urlopen(self.list_url).read()
		lr = re.compile(r'<a href="(.*?)" target=_blank>')
		return re.findall(lr,info)

	#
	#
	def choose_Video(self,video_list):
		self.source_id = random.sample(video_list,1)[0].split("=")[1]
		return self.source_id

	#检测登录状态的函数
	#登录状态存在返回True,否则返回False
	def check_login(self):
		res = self.opener.open(self.check_url).read()
		return "计时统计" in res

	#*******************
	#	欺骗函数
	#*******************
	#
	#@param status 要欺骗的状态  开始记时为'ADD',停止记时为'STOP'
	#
	def cheat(self,status):
		referer = "http://ocw.swjtu.edu.cn/websys/videoview.jsp?resource_id=%s" % self.source_id
		time_url = "http://ocw.swjtu.edu.cn/servlet/UserStudyRecordAction?resource_id=%s" % self.source_id
		time_url += "&SetType=%s&ranstring=&" % status
		if "STOP" == status:
			time_url += "sid=%s&tt=%s" % (self.sid,str(int(time.time())))
		time_url += "sid=&tt=%s" % str(int(time.time()))
		request = urllib2.Request(time_url)
		request.add_header("Referer",referer)
		request.add_header("User-Agent","ozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.111 Safari/537.36")
		rs = self.opener.open(request).read()
		sr = re.compile(r'<select_message>(.*?)</select_message>')
		smg = re.findall(sr,rs)[0]
		if "ADD" == status:
			self.sid = smg
		return smg
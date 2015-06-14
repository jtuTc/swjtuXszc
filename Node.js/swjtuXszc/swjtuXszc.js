exports.swjtuXszc = function(username,password){this.username = username;this.password = password;}
exports.swjtuXszc.prototype = {
	cookie : "",
	sourceID : "",
	host : "ocw.swjtu.edu.cn",
	loginUrl : "/servlet/UserLoginDataAction",
	checkUrl : "/websys/studentstudytime.jsp",
	listUrl : "/page/viewVideo.jsp?c_id=196",
	listener : new (require('events').EventEmitter)(),
	login : function(){
		var self = this;
		var param = {UserName:self.username,Password:self.password,UserType:"stu"};
		var request = require("http").request({
			host : self.host,
			path : self.loginUrl,
			port : 80,
			method : "POST",
			headers : {"Content-Type" : "application/x-www-form-urlencoded"}
		},function(response){
			var str = "";
			response.on("data",function(chunk){
				str += chunk;
			});
			response.on("end",function(){
				if( str.indexOf("成功") >= 0){
					self.listener.emit("success",{type:"login",message:"login sucess"});
					self.cookie = response.headers['set-cookie'][0].split(";")[0].split("=")[1];
					self.listener.emit("success",{type:"cookie",message:self.cookie});
				}
				else self.listener.emit("fail",{type:"login",message:"login fail"});
			});
		})
		request.write(require("querystring").stringify(param));
		request.end();
		return self;
	},
	chooseSourceID : function(videolist){
		var sourceid = videolist[Math.round(Math.random()*videolist.length)].split("=")[1];
		this.sourceID = sourceid;
		this.listener.emit("success",{type:"sourceID",message:sourceid});
	},
	grapList : function(){
		var self = this;
		var request = require("http").request({
			host : self.host,
			path : self.listUrl,
			port : 80,
			method : "GET"
		},function(response){
			var str = "";
			response.on("data",function(chunk){
				str += (new Buffer(chunk)).toString();
			});
			response.on("end",function(){
				var videolist = [];
				var r1 = /<a href="(.*?)" target=_blank>/g;
				var r2 = /<a href="(.*?)" target=_blank>/;
				var tmpl = str.match(r1);
				for(var i in tmpl){
					videolist.push(tmpl[i].match(r2)[1]);
				}
				self.listener.emit("success",{type:"grapList",message:videolist});
			});
		})
		request.end();
	},
	cheat : function(type){
		var self = this;
		var bufferHelper = require("./bufferhelper");
		var path = "/servlet/UserStudyRecordAction?resource_id=" + self.sourceID;
		if("ADD" == type)path += "&SetType=ADD&ranstring=&sid=&tt=" + (new Date()).getTime();
		else if("STOP" == type)path += "&SetType=STOP&ranstring=&sid=" + self.sid + "&tt=" + (new Date()).getTime();
		console.log(path)
		var request = require("http").request({
			host : self.host,
			path : path,
			port : 80,
			method : "GET",
			headers : {
				"Cookie" : "JSESSIONID=" + self.cookie,
				"User-Agent" : "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.111 Safari/537.36",
				"Referer" : "http://ocw.swjtu.edu.cn/websys/videoview.jsp?resource_id=" + self.sourceID
			}
		},function(response){
			var bufferhelper = new bufferHelper();
			response.on("data",function(chunk){
				bufferhelper.concat(chunk);
			});
			response.on("end",function(){
				var iconv = require("./iconv");
				var destr = iconv.decode(bufferhelper.toBuffer(),"GBK");
				var sid = destr.match(/<select_message>(.*?)<\/select_message>/)[1];
				self.sid = sid; 
				self.listener.emit("success",{type:"cheat",message:sid}); 
			});
		});
		request.end();
	},
	checkLogin : function(){
		var self = this;
		var request = require("http").request({
			host : self.host,
			path : self.checkUrl,
			port : 80,
			method : "GET",
			headers : {
				"Cookie" : "JSESSIONID=" + self.cookie + ";"
			}
		},function(response){
			var str = "";
			response.on("data",function(chunk){
				str += (new Buffer(chunk)).toString();
			});
			response.on("end",function(){
				if(str.indexOf("学时") >= 0)self.listener.emit("success",{type:"checkLogin",message:"success"});
				else self.listener.emit("fail",{type:"checkLogin",message:"fail"});
			});
		});
		request.end();
	}
}
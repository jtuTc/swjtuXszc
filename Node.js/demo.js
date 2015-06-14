var swjtuXszc = require("./swjtuXszc").swjtuXszc;
var username = "";
var password = "";
var sx = new swjtuXszc(username,password);
var timer = 0;
var videoList = [];
sx.listener.on("success",function(e){
	switch(e.type){
		case "sourceID":{
			sx.login();
		}
		break;
		case "cookie":{
			sx.cheat("ADD");
		}
		break;
		case "grapList":{
			videoList = e.message;
			sx.chooseSourceID(videoList);
		}
		break;
		default:{
			console.log(e.type + " : " + e.message);
		}
	}
});
sx.grapList();
var handle = setInterval(function(){
	timer += 5;
	if(timer > 150){
		sx.cheat("STOP");
		clearInterval(handle);
	}
	sx.checkLogin();
	console.log(150 - timer + " seconds left!");
},5000);
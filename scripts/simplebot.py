
from includes import *


class SimpleBot(Bot):
	def about(self):
		return "SimpleBot"
	
	def author(self):
		return "Vladislav Rassokhin <vladrassokhin@gmail.com>"
	
	def run(self):
		print "This is the SimpleBot main cycle"
		print "this bot just print this text and test system variables"
		print "Bot interface", Bot
		print "Util provider", PUtil
		print "Config provider", PConfig
		print "Player provider", PPlayer
		return
	

Manager.registerBot("simplebot", SimpleBot)

# coding=UTF-8

from includes import *
from apiv1e import *

class Tester1Bot(Bot):
    def about(self):
        return "APIv1 Tester 1"

    def author(self):
        return "Vladislav Rassokhin <vladrassokhin@gmail.com>"

    def run(self):
        print("This bot just tests basic dunctions (APIv1) (modules 'apiv1' and 'apiv1e')")
        print "You position", my_coord()
        print "And again", my_coord_x(), my_coord_y()
        a = input_get_object("Select some tree not far from you")
        print "now i'm going to interact with it, test sleep while moving"
        map_move(a, 0, 0); sleep(500); wait_end_move()
        map_interact_click(a, 0)
        print "let's wait for context menu"
        sleep(1000)
        #wait_context_menu()
        print "taking a branch"
        select_context_menu("Take branch")
        wait_hourglass()
        drop(1)
        print "now i will try to show you Inventory"
        if not have_inventory("Inventory"):
            open_inventory()
        while not have_inventory("Inventory"):
            sleep(100)
        set_inventory("Inventory")
        while next_item() == 1 :
            print "item quality:" + item_quality()
            if is_item_name("branch"):
                print "It's branch! Take it out"
                item_click("take")
                drop(1)
        print "so, all branches are gone"
        print "Bye!"
        return


Manager.registerBot("t1b", Tester1Bot)

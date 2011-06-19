from apiv1e import *

__author__ = 'vlad'

from includes import Bot, Manager, PPlayer


class Digger1Bot(Bot):
    def about(self):
        return "Digger Adopted Bot"

    def author(self):
        return "ark.su forum"

    def run(self):
        print "starting digger"
        print "inventory must be on screen"
        if not have_inventory("Inventory"):
            open_inventory()
        while not have_inventory("Inventory"):
            sleep(100)
        set_inventory("Inventory")
        while getHungry() > 60:
            print "you not hungry, it's good"
            if PPlayer.getStamina < 60:
                print "i need water"
                inventory("Inventory", 0, 0, "take")
                wait_drag()
                set_item_drag()
                if not is_item_name("water"):
                    map_interact_click(find_object_by_name("well", 3), 0)
                    while not is_item_name("water"):
                        sleep(100)
                inventory("Inventory", 5, 3, "itemact")
                sleep(1000)
                item_drop(0, 0)
                wait_drop()
                inventory("Inventory", 5, 3, "iact")
                wait_context_menu()
                select_context_menu("Drink")
                wait_hourglass()
            print "plowing..."
            send_action("plow")
            wait_dig_cursor()
            map_click(0, 0, 1, 0)
            wait_hourglass()
            print "plowing..."
            send_action("plow")
            wait_dig_cursor()
            map_click(0, 0, 1, 0)
            wait_hourglass()
            print "plowing..."
            send_action("plow")
            wait_dig_cursor()
            map_click(0, 0, 1, 0)
            wait_hourglass()
            # eohungry cycle
        return


Manager.registerBot("digger1", Digger1Bot)

  
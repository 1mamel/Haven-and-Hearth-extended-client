# coding=UTF-8

from apiv1e import *

class BarrelFiller(Bot):
    def about(self):
        return "Barrels Filler"

    def author(self):
        return "Vladislav Rassokhin <vladrassokhin@gmail.com> & Ark.SU forum"

    def fill(self):
        is_empty = False
        bucketPos = None
        print ("I will fill barrels from current position")
        #        source = input_get_object("Select source")
        destCount = int(PUI.askWait("Destinations count"))
        dest = []
        destFull = []
        for i in xrange(0, destCount):
            dest[i] = input_get_object("Select water destination " + (i + 1))
            destFull[i] = False

        for i in xrange(0, destCount):
            if set_inventory("Inventory"):
                while next_item():
                    if is_item_name("bucket"):
                        if is_item_tooltip("Empty"): is_empty = True;
                        bucketPos = item_coord()
                        item_click("take")
                        wait_drag()
                        break

            while not destFull[i]:
                # если пустое ведро - наполняем водой
                if is_empty:
                    map_abs_interact_click(self.startPosition.x, self.startPosition.y, 0)
                    set_item_drag()
                    while is_item_name("buckete"): sleep(100);
                map_interact_click(dest[i], 0)
                time = 0
                while not isMoving():
                    # ждем 1 секунду
                    sleep(100)
                    time += 100
                    if time > 1000:
                        # значит бочка полная (персонаж не двигается если бочка полная)
                        destFull[i] = True
                        break

                if destFull[i]:
                    print ("Destination " + (i + 1) + " filled")
                    break
                while 1:
                    wait_end_move()
                    sleep(300)
                    if not isMoving():
                        break
                is_empty = True

            if isDraggingItem():
                item_drop_to_inventory("Inventory", bucketPos.x, bucketPos.y)
                wait_drop()
            map_abs_click(self.startPosition.x, self.startPosition.y, 1, 0)
            sleep(100)
            wait_end_move()
        print ("Filling completed!")


    def run(self):
        # запоминаем где я стою
        self.startPosition = PPlayer.getPosition()
        fill(self)



Manager.registerBot("filler", BarrelFiller)

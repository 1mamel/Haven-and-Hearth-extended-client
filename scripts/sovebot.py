# coding=UTF-8
from apiv1 import is_cursor

from includes import *
from apiv1e import *

class MB(Bot):
    height = 8
    #    myx = None
    #    myy = None
    startPosition = None
    bar1 = None
    bar2 = None
    bar1_full = 0
    bar2_full = 0
    boat = None

    plow = None
    seed_x = None
    seed_y = None

    flower_stage = 3
    is_grass = 1


    def take_seed(self):
        # ищем нужное семечко
        if set_inventory("Inventory"):
            while next_item():
                # берем в руки нужное семечко из инвентаря
                if is_item_name("flaxseed"):
                    self.seed_x = item_coord_x()
                    self.seed_y = item_coord_y()
                    item_click("take")
                    wait_drag()
                    break


    def drop_seed(self):
        item_drop_to_inventory("Inventory", self.seed_x, self.seed_y)
        wait_drop()


    def process_harvest_tile(self, x, y):
        processed = False
        id = find_map_object("", 2, x, y)

        if id > 0:
            stage = get_object_blob(id, 0)
            if stage >= flower_stage:
                processed = 1
                if not is_cursor("harvest"):
                    send_action("harvest")
                    wait_harvest_cursor()
                    # собираем урожай
                do_click(id, 1, 0)
                if x != 0 or y != 0:
                    wait_move()
                wait_hourglass()

        if not processed and (x != 0) or (y != 0):
            reset_cursor()
            map_move_step(x, y)
            wait_move()


    def process_seed_tile(self, x, y):
        if is_grass:
            if not is_cursor("dig"):
                send_action("grass")
                wait_dig_cursor()

        id = 0# find_map_object("flax", 2, x,y);
        # если в нужном тайле ниче не растет - нужно засадить
        if not id:
            # если в руках ничего нет
            if not DraggingItem:
                take_seed()
                # сажаем
            map_interact_click(x, y, 1)
            # если сажаем под собой то никуда не побежим - просто ждем
            if x == 0 and y == 0:
                sleep(700)
            else:
                wait_move()
            map_click(0, 0, 1, 0)
            sleep(300)
        else:
            if x != 0 or y != 0:
                # бросаем семечко в инвентарь
                if DraggingItem:
                    drop_seed(self)
                map_move_step(x, y)
                wait_move()


    # собрать урожай
    def harvest(self):
        process_harvest_tile(0, 0)
        #for (i = 0; i<(height-1); i++):
        for _ in xrange(0, height - 1):
            process_harvest_tile(0, 1)

        process_harvest_tile(1, 0)
        #   for (i = 0; i<(height-1); i++):
        for _ in xrange(0, height - 1):
            process_harvest_tile(0, -1)

        reset_cursor()
        map_move_step(-1, 0)
        wait_move()


    # посадить семена
    def seed(self):
        process_seed_tile(0, 0)
        for _ in xrange(0, height - 1):
            process_seed_tile(0, 1)
        process_seed_tile(1, 0)
        for _ in xrange(0, height - 1):
            process_seed_tile(0, -1)

        # бросаем семечко в инвентарь
        if DraggingItem:
            item_drop_to_inventory("Inventory", self.seed_x, self.seed_y)
            wait_drop()
        reset_cursor()


    def plow_block(self):
        do_click(plow, 3, 0)
        wait_move()
        wait_dig_cursor()

        map_move(plow, 0, (height - 1) * 11)
        wait_move()
        map_move(plow, 11, 0)
        wait_move()
        map_move(plow, 0, -(height - 1) * 11)
        wait_move()
        map_move(plow, 11, 0)
        wait_move()
        reset_cursor()
        map_move_step(-1, 0)
        wait_move()


    def fill_food(self):
        id = input_get_object("Select food")
        take_seed()
        map_interact_click(id, 1, None)
        wait_move()

        while 1:
            map_interact_click(id, 1, None)
            sleep(300)


    def take_fibres(self):
        #   for (i=0; i<16; i++):
        for _ in range(0, 16):
            id = find_map_object("flaxfibre", 2, 0, 1)
            if id > 0:
                do_click(id, 3, 0)
                sleep(800)
            else:
                map_move_step(0, 1)
                wait_move()

        send_action("bp", "htable")
        sleep(500)
        map_place(0, 2, 1, 0)
        while not BuildReady: sleep(100);

        #   for (i=0; i<8; i++):
        for _ in range(0, 8):
            if set_inventory("Inventory"):
                while next_item():
                    if is_item_name("flaxfibre"):
                        item_click("transfer")
                        sleep(500)
                        break

        map_move_step(0, -1)
        wait_move()
        send_action("bp", "htable")
        sleep(500)
        map_place(0, 1, 1, 0)
        while not BuildReady: sleep(100);

        #   for (i=0; i<8; i++):
        for _ in range(0, 8):
            if set_inventory("Inventory"):
                while next_item():
                    if is_item_name("flaxfibre"):
                        item_click("transfer")
                        sleep(500)
                        break

        map_move_step(1, -16)
        wait_move()


    def run(self):
        if not have_inventory("Inventory"):
            open_inventory()
            while not have_inventory("Inventory"): sleep(300);

        # запоминаем где я стою
        self.startPosition = PPlayer.getPosition()


        #fill_food();
        # while (1):
        # map_click(0,-1,1,0);
        # wait_move();
        # }

        #take_fibres();
        # plow = input_get_object("Select plow");
        # while (1):
        # check_stamina();
        # #plow_block();
        # #harvest();
        # seed();
        # }


Manager.registerBot("sovebot", MB)

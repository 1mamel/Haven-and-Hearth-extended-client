# coding=UTF-8

from apiv1e import *

class Drinker(Bot):
    def about(self):
        return "Barrels Filler"

    def author(self):
        return "Vladislav Rassokhin <vladrassokhin@gmail.com> & Ark.SU forum"


    # проверяем стамину & выпить воды из ведра
    def drink(self):

        if getStamina() > 75:
            return False

        bucket_x = 0
        bucket_y = 0
        # если мой инвентарь открыт
        if set_inventory("Inventory"):
            # ищем ведро с водой
            while next_item():
                if is_item_name("bucket-water"):
                    bucket_x = item_coord_x()
                    bucket_y = item_coord_y()

                    # берем в руки
                    item_click("take")
                    # ждем пока не возьмеца в руки
                    wait_drag()
                    break

            # нужно искать по новой
            reset_inventory()
            # ищем фляжку
            while next_item():
                if is_item_name("waterflask"):
                    # наполняем ее водой из ведра
                    item_click("itemact")
                    sleep(500)
                    # кладем откуда взяли
                    item_drop(bucket_x, bucket_y)
                    # ждем пока положится
                    wait_drop()
                    # вызываем конекстное меню у фляжки
                    item_click("iact")
                    # ждем его
                    wait_context_menu()
                    # выбираем пить
                    select_context_menu("Drink")
                    # ждем когда полностью напьеца
                    wait_hourglass()
                    # выходим из поиска
                    return True
        return False

    def run(self):
        while self.drink():
            sleep(50)


Manager.registerBot("drinker", Drinker)
Manager.alias("drink", "drinker")
# coding=UTF-8

# Basic functions
# Thanks to ark.su for simple basic functions

from includes import *

def set_bot1(name):
    PConfig.bot1 = name


def set_bot2(name):
    PConfig.bot2 = name


def sleep(seconds):
    import time

    time.sleep(seconds)

# print already exists

def exit(obj):
    Manager.kill(obj)


def logout():
    return False # TODO


def say(s):
    PPlayer.sayArea(s)


# послать клик по объекту на карте. объект указывается по objid. кнопка мыши btn (1 - левая. 3 - правая). дополнительные флаги mod (1-шифт. 2-ктрл. 4-альт. 8-вин)
def do_click(objectId, button, mode):
    PPlayer.mapCLick(objectId, button, mode)

# простой клик по карте. как обычно щелкаем мышью. только координаты относительно игрока
def map_click(x, y, button, mode):
    PPlayer.mapClick(x, y, button, mode)

# абсолютный клик по карте. как обычно щелкаем мышью. указываем координаты мира
def map_abs_click(x, y, btn, mode):
    PPlayer.mapAbsoluteClick(x, y, button, mode)

# передвинутся на указанное количество тайлов от текущей позиции игрока, фактически это map_click всегда с левой кнопкой
def map_move_step(x, y):
    PPlayer.moveStep(x, y)

# бежать к указанной точке. объект и оффсет от него в координатах карты. (1 тайл = 11 точек)
def map_move(objid, x, y):
    PPlayer.move(x, y)

# Player coordinates
# получить мои мировые координаты (абсолютные)
def my_coord():
    return PPlayer.coord


def my_coord_x():
    return PPlayer.x


def my_coord_y():
    return PPlayer.y


# кликнуть по карте (взаимодействие, чтото держим в руках) координаты указываются в тайлах от текущей позиции игрока
def map_interact_click(x, y, mode):
    return


def map_interact_click(objid, mode):
    return

# то же самое только указываем абсолютные координаты
def map_abs_interact_click(x, y, mod):
    return

# поставить объек который хотим построить в указанные координаты относительно игрока. координаты задаем в тайлах
def map_place(x, y, btn, mod):
    return

# выбрать опцию в контекстном выпадающем меню над объектом
def select_context_menu(option_name):
    return


# послать действие из меню внизу справа, с одним параметром. параметр видно при щелчке на кнопку в логе по ф12
# laystone - укладка камней. асфальтирование
# carry - перетаскивание объектов (lift)
def send_action(name):
    return

# послать действие из меню внизу справа, если нужно указывать 2 параметра
def send_action(name, name2):
    return

# проверяет стоит ли указанный курсор в данный момент
# dig - лопата
# chi - курсор пипетка. появляется когда хотим чето перетаскивать. и надо указать объект
def is_cursor(cursor_name):
    return True # boolean

# дропнуть вещь в руках на землю
def drop(mod):
    return

# сказать игроку выбрать объект мышкой. все объекты подсвечиваются зеленым. пользователь должен щелкнуть на какой нить объект тогда управление вернется в скрипт
# msg - выведет сообщение красным цветом на экран
def input_get_object(msg):
    return 0 #integer

# найти объект по имени, проверяется вхождение имени в имя ресурса, радиус в тайлах
def find_object_by_name(name, radius):
    return 0 #integer


# найти объект по типу, радиус в тайлах. доступные типы:
#   tree - дерево
def find_object_by_type(type, radius):
    return 0 #integer


# найти объект по оффсету от себя в заданном радиусе и с заданным именем. ВНИМАНИЕ!!! радиус в точках карты. отступ в тайлах
# если имя не указано (пустая строка) ищет любой объект в заданном радиусе
def find_map_object(name, radius, x, y):
    return 0 #integer

# проверить наличие инвентаря по имени
def have_inventory(name):
    return 0 #integer

#открыть мой инвентарь
def open_inventory():
    return

#поставить текущий инвентарь, после автоматически сбрасывается итератор
def set_inventory(name):
    return

#сбросить итератор итемов в инвентаре
def reset_inventory():
    return

#вызывать итератор для установки итема
#0 - если вещи нет (прошли весь список). 1 если есть
def next_item():
    return 0 #integer

# получить количество вещей в списке
def get_items_count():
    return 0 #integer


# установить текущую вещь по индексу в списке
def set_item_index(index):
    return

# установить текущей вещью - то что держим в руках если оно есть. (чтобы получить ее параметры)
def set_item_drag():
    return

# установить текущей вещью - вещь из инвентаря. указываем индекс (чтобы получить ее параметры)
def set_item_equip(index):
    return

# получаем параметры текущей вещи установленной итератором

# - 0 или 1 совпадает ли имя вещи
def is_item_name(name):
    return 0


def is_item_tooltip(name):# - 0 или 1 совпадает ли тултип (всплывающая подсказка на вещи)
    return 0


def item_quality():# - возвращает качество
    return 10


def item_click(action):# - щелчок по вещи
    return


def item_click(action, mod):# - щелчок по вещи, с модификатором клавиатуры
    return

#  команды (action):
#    take - взять вещь
#    itemact - взаимодействие на вещь. чтото держим в руках и щелкаем правой кнопкой по вещи
#    transfer - переместить. щелчек лкм с зажатым шифтом
#    iact - фактически правый щелчек по вещи. для вызова контекстного меню
#    drop - дропнуть вещь на землю. щелчек лкм с зажатым контролом

# координаты вещи в инвентаре
def item_coord():
    return (0, 0) #(int,int)


def item_coord_x():
    return 0 #integer


def item_coord_y():
    return 0 #integer


#  - получить цифру возле итема. пример: стадия червяков шелкопрядов. рисуетя вверху слева у вещи.
def item_num():
    return 0 #integer

# - получить прогресс итема. пример: сушка. круглешок на шкуре. принимаем значения от 0 до 100.
def item_meter():
    return 0 #integer

#  - положить вещь которую держим в руках в текущий инвентарь установленный set_inventory. в указанные координаты. нумерация клеток с нуля
def item_drop(x, y):
    return


# тоже самое но с модификатором клавиатуры
# дать команду вещи в инвентаре с указаныым именем. по указанным координатам вещи в этом инвентаре.
# take - взять вещь
# itemact - взаимодействие на вещь. чтото держим в руках и щелкаем правой кнопкой по вещи
# transfer - переместить. щелчек лкм с зажатым шифтом
# iact - фактически правый щелчек по вещи. для вызова контекстного меню
# drop - дропнуть вещь на землю. щелчек лкм с зажатым контролом

def inventory(name, x, y, action):
    return


def inventory(name, x, y, action, mode):
    return


# дропнуть вещь в указанный инвентарь, допустим когда надо из моего инвентаря не закрывая его дропнуть в шкаф или ящик
def item_drop_to_inventory(name, x, y):
    return


# проверить есть ли окно крафта с указанным заголовком
def check_craft(wnd):
    return 0 #integer

# подождать появления и готовности окна с указанным заголовком
def wait_craft(wnd):
    return

# скрафтить вещь. all - 0 или 1. если 1 - то крафтим все. если 0 только одну вещь. перед крафтом надо каким то образом открыть окно крафта
def craft(all):
    return

#дать команду в эквип (одевалку), указываем какому слоту даем команду и саму команду
#слоты: hh_slots.png
#команды:
#   take - взять вещь из слота
#   itemact - взаимодействие на вещь. чтото держим в руках и щелкаем правой кнопкой по вещи
#   transfer - переместить. щелчек лкм с зажатым шифтом
#   iact - фактически правый щелчек по вещи. для вызова контекстного меню
#   drop - дропнуть вещь которую держим в руках в указанный слот.
def equip(slot, action):
    return


#включить/выключить (0 или 1) рендер в клиенте. нужно для экономии ресурсов
def render_mode(enabled):
    return

#получить данные из мессаги объекта. именно так задаются стадрии роста ( пример: get_object_blob(34676844, 0) вернет стадию роста указанного объекта если там есть данные. либо вернет 0)
def get_object_blob(id, index):
    return 0 #integer

# сбросить итератор бафов
def reset_buff():
    return

# перейти на следующий элемент баф листа. вернет либо 0 если уже конец. либо 1 если перешли на след бафф
def next_buff():
    return 0 #integer

# вернет показатель шкалы под баффом (от 0 до 100)
def buff_meter():
    return 0 #integer

# вернет оставшееся время до истечения баффа (от 0 до 100), чем ближе к 0 тем меньше времени осталось
def buff_time_meter():
    return 0 #integer

# проверить имя баффа на вхождение строки. вернет 0 или 1
def is_buff_name(name):
    return 0 #integer


# нажать кнопку строительства в окошке билда
def build_click():
    return

#игровые переменные:
#    int HourGlass - 0 или 1 - есть песочные часы или нет
#    int Hungry - абсолютное значение голода
#    int HP - хп
#    int Stamina - стамина
#    int PlayerID - ид моего чара
#    int Moving - 0 или 1 двигается ли мой персонаж
#    int ContextMenuReady - 0 или 1 готово ли контекстное меню к работе (полностью раскрыто и готово к приему команды)
#    int DraggingItem - 0 или 1 есть ли вещь в руках. перетаскиваем ли чего нибудь. НЕ над чаром. а в руках. то что бегает за курсором.
#    int CraftReady - 0 или 1 готово ли окно крафта к приему команды
#    int BuildReady - есть ли окно билда чего либо. 0 или 1









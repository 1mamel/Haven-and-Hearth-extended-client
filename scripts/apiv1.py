# coding=UTF-8

__author__ = 'Vladislav Rassokhin <vladrassokhin@gmail.com>'

# Basic functions
# Thanks to ark.su for simple basic functions

#noinspection PyUnresolvedReferences
import time


#noinspection PyUnresolvedReferences
#noinspection PyUnresolvedReferences
import haven.scriptengine.providers.Config as PConfig
import haven.scriptengine.providers.Player as PPlayer
import haven.scriptengine.providers.Util as PUtil
import haven.scriptengine.providers.UIProvider as PUI
import haven.scriptengine.providers.MapProvider as PMap
import haven.scriptengine.providers.InventoriesProvider as PInv
import haven.scriptengine.providers.CraftProvider as PCraft
import haven.scriptengine.providers.BuffsProvider as PBuffs
#noinspection PyUnresolvedReferences

def set_bot1(name):
    PConfig.bot1 = name


def set_bot2(name):
    PConfig.bot2 = name


# Sleep some seconds
def sleep(milliseconds):
    time.sleep(milliseconds / 1000.0)

# Logout
def logout():
    PUtil.logout()

# Say something into area chat
def say(s):
    PPlayer.sayAreaChat(s)


# послать клик по объекту на карте. объект указывается по objid. кнопка мыши btn (1 - левая. 3 - правая). дополнительные флаги mod (1-шифт. 2-ктрл. 4-альт. 8-вин)
def do_click(objectId, button, mode):
    PMap.click(objectId, button, mode)

# простой клик по карте. как обычно щелкаем мышью. только координаты относительно игрока
def map_click(x, y, button, mode):
    PMap.click(x, y, button, mode)

# абсолютный клик по карте. как обычно щелкаем мышью. указываем координаты мира
def map_abs_click(x, y, button, mode):
    PMap.clickAbs(x, y, button, mode)

# передвинутся на указанное количество тайлов от текущей позиции игрока, фактически это map_click всегда с левой кнопкой
def map_move_step(x, y):
    PPlayer.moveStep(x, y)

# бежать к указанной точке. объект и оффсет от него в координатах карты. (1 тайл = 11 точек)
def map_move(objid, x, y):
    PPlayer.move(objid, x, y)

# Player coordinates
# получить мои мировые координаты (абсолютные)
def my_coord():
    return PPlayer.getPosition()


def my_coord_x():
    return PPlayer.getPosition().x


def my_coord_y():
    return PPlayer.getPosition().y


# кликнуть по карте (взаимодействие, чтото держим в руках) координаты указываются в тайлах от текущей позиции игрока
#def map_interact_click(x, y, mode):
#    return PMap.interactClick(x, y, mode)


# то же самое только указываем абсолютные координаты
def map_abs_interact_click(x, y, mode):
    return PMap.interactClickAbs(x, y, mode)


def map_interact_click(first, second, third):
    if third is None:
        return PMap.interactClickObj(first, second)  #assume as objid, mode
    return PMap.interactClick(first, second, third) #assume as x, y, mode


# поставить объект который хотим построить в указанные координаты относительно игрока. координаты задаем в тайлах
def map_place(x, y, btn, mode):
    return PMap.place(x, y, btn, mode)


# выбрать опцию в контекстном выпадающем меню над объектом
def select_context_menu(option_name):
    return PUI.selectFlowerMenuOpt(option_name)


# послать действие из меню внизу справа, с одним параметром. параметр видно при щелчке на кнопку в логе по ф12
# laystone - укладка камней. асфальтирование
# carry - перетаскивание объектов (lift)
#def send_action(name):
#    return PUI.sendAction(name)


# послать действие из меню внизу справа, если нужно указывать 2 параметра
def send_action(name, name2=""):
    return PUI.sendAction(name, name2)


# проверяет стоит ли указанный курсор в данный момент
# dig - лопата
# chi - курсор пипетка. появляется когда хотим чето перетаскивать(lift). и надо указать объект
def is_cursor(cursor_name):
    return PInv.isCursorNameContains(cursor_name)


# дропнуть вещь в руках на землю
def drop(mode):
    return PMap.drop(mode)


# сказать игроку выбрать объект мышкой. все объекты подсвечиваются зеленым. пользователь должен щелкнуть на какой нить объект тогда управление вернется в скрипт
# msg - выведет сообщение на экран
def input_get_object(msg):
    return PUI.inputObject(msg)


# найти объект по оффсету от себя в заданном радиусе и с заданным именем. ВНИМАНИЕ!!! радиус в точках карты. отступ в тайлах
# если имя не указано (пустая строка) ищет любой объект в заданном радиусе
def find_map_object(name, radius, x, y):
    return PMap.findObjectByName(name, radius, x, y)


# найти объект по имени, проверяется вхождение имени в имя ресурса, радиус в тайлах
# как и предыдущее
def find_object_by_name(name, radius):
    return PMap.findObjectByName(name, radius)


# найти объект по типу, радиус в тайлах. доступные типы:
#   tree - дерево
def find_object_by_type(type, radius):
    return PMap.findObjectByType(type, radius)


# проверить наличие инвентаря по имени
def have_inventory(name):
    return PInv.haveInventory(name)


#открыть мой инвентарь
def open_inventory():
    return PInv.toggleUserInventory()


#поставить текущий инвентарь, после автоматически сбрасывается итератор
def set_inventory(name):
    return PInv.setInventory(name)


#сбросить итератор итемов в инвентаре
def reset_inventory():
    return PInv.resetInventoryIter()


#вызывать итератор для установки итема
#0 - если вещи нет (прошли весь список). 1 если есть
def next_item():
    return PInv.nextInventoryItem()


# получить количество вещей в списке
# -1 если не установлен инвентарь
def get_items_count():
    return PInv.getInventoryItemsCount()


# установить текущую вещь по индексу в списке
def set_item_index(index):
    return PInv.useInventoryItem(index)


# установить текущей вещью - то что держим в руках если оно есть. (чтобы получить ее параметры)
def set_item_drag():
    return PInv.useDraggingItem()


# установить текущей вещью - вещь из инвентаря. указываем индекс (чтобы получить ее параметры)
def set_item_equip(index):
    return PInv.useEquipItem(index)


# получаем параметры текущей вещи установленной итератором

# - boolean совпадает ли имя вещи
def is_item_name(name):
    return PInv.isCurrentItemNameContains(name)


# - boolean совпадает ли тултип (всплывающая подсказка на вещи)
def is_item_tooltip(name):
    return PInv.isCurrentItemTooltipContains(name)


# - возвращает качество
def item_quality():
    return PInv.getCurrentItemQuality()


# - щелчок по вещи, с модификатором клавиатуры
def item_click(action, mod=0):
    return PInv.clickItem(action, mod)

#  команды (action):
#    take - взять вещь
#    itemact - взаимодействие на вещь. чтото держим в руках и щелкаем правой кнопкой по вещи
#    transfer - переместить. щелчек лкм с зажатым шифтом
#    iact - фактически правый щелчек по вещи. для вызова контекстного меню
#    drop - дропнуть вещь на землю. щелчек лкм с зажатым контролом

# координаты вещи в инвентаре
def item_coord():
    return PInv.getInventoryItemCoord() # return Coord class


def item_coord_x():
    return PInv.getInventoryItemCoord().x # integer


def item_coord_y():
    return PInv.getInventoryItemCoord().y # integer


#  - получить цифру возле итема. пример: стадия червяков шелкопрядов. рисуетя вверху слева у вещи.
def item_num():
    return PInv.getItemQuantity() #integer

# - получить прогресс итема. пример: сушка. круглешок на шкуре. принимаем значения от 0 до 100.
def item_meter():
    return PInv.getItemMeter() #integer

#  - положить вещь которую держим в руках в текущий инвентарь установленный set_inventory. в указанные координаты. нумерация клеток с нуля
def item_drop(x, y):
    return PInv.dropItemIntoCurrentInventory(x, y)


# дать команду вещи в инвентаре с указаныым именем. по указанным координатам вещи в этом инвентаре.
# take - взять вещь
# itemact - взаимодействие на вещь. чтото держим в руках и щелкаем правой кнопкой по вещи
# transfer - переместить. щелчек лкм с зажатым шифтом
# iact - фактически правый щелчек по вещи. для вызова контекстного меню
# drop - дропнуть вещь на землю. щелчек лкм с зажатым контролом

#def inventory(name, x, y, action):
#    return PInv.doInventoryAction(name, x, y, action, 0)

# тоже самое но с модификатором клавиатуры
def inventory(name, x, y, action, mode=0):
    return PInv.doInventoryAction(name, x, y, action, mode)


# дропнуть вещь в указанный инвентарь, допустим когда надо из моего инвентаря не закрывая его дропнуть в шкаф или ящик
def item_drop_to_inventory(name, x, y):
    return PInv.dropItemIntoInventory(name, x, y)


# проверить есть ли окно крафта с указанным заголовком
def check_craft(wnd):
    return PCraft.isOpened(wnd)

# подождать появления и готовности окна с указанным заголовком
def wait_craft(wnd):
    while not PCraft.isOpened(wnd):
        time.sleep(0.2)


# скрафтить вещь. all - True или False. если True - то крафтим все. False только одну вещь. перед крафтом надо каким то образом открыть окно крафта
def craft(all):
    return PCraft.craft(all)


# скрафтить все. False только одну вещь. перед крафтом надо каким то образом открыть окно крафта
def craftAll():
    return PCraft.craft(True)


#дать команду в эквип (одевалку), указываем какому слоту даем команду и саму команду
#слоты: hh_slots.png
#команды:
#   take - взять вещь из слота
#   itemact - взаимодействие на вещь. чтото держим в руках и щелкаем правой кнопкой по вещи
#   transfer - переместить. щелчек лкм с зажатым шифтом
#   iact - фактически правый щелчек по вещи. для вызова контекстного меню
#   drop - дропнуть вещь которую держим в руках в указанный слот.
def equip(slot, action):
    return PUI.equipAction(slot, action)


#включить/выключить (True или False) рендер в клиенте. нужно для экономии ресурсов
def render_mode(enabled):
    return PUI.setRenderMode(enabled)


#получить данные из мессаги объекта. именно так задаются стадрии роста ( пример: get_object_blob(34676844, 0) вернет стадию роста указанного объекта если там есть данные. либо вернет 0)
def get_object_blob(id, index):
    return PMap.getObjectBlob(id, index)


# сбросить итератор бафов
def reset_buff():
    return PBuffs.resetBuffsIterator()


# перейти на следующий элемент баф листа. вернет либо 0 если уже конец. либо 1 если перешли на след бафф
def next_buff():
    return PBuffs.nextBuff()


# вернет показатель шкалы под баффом (от 0 до 100)
def buff_meter():
    return PBuffs.getBuffMeter()


# вернет оставшееся время до истечения баффа (от 0 до 100), чем ближе к 0 тем меньше времени осталось
def buff_time_meter():
    return PBuffs.getBuffTimeMeter()


# проверить имя баффа на вхождение строки. вернет 0 или 1
def is_buff_name(name):
    return PBuffs.isBuffNameContains(name)


# нажать кнопку строительства в окошке билда
def build_click():
    return PUI.buildClick()


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

def isHourGlass():
    return PPlayer.isInProgress()


def getHungry():
    return PPlayer.getHungry()


def getHp():
    return PPlayer.getHP()


def getHpSoft():
    return PPlayer.getHPSoft()


def getHpHard():
    return PPlayer.getHPHard()


def getStamina():
    return PPlayer.getStamina()


def getPlayerID():
    return PPlayer.getId()


def isMoving():
    return PPlayer.isMoving()


def isContextMenuReady():
    return PUI.isFlowerMenuReady()


def isDraggingItem():
    return PInv.haveDragItem()


def isCraftReady():
    return PCraft.isReady()


def isBuildReady():
    return PUI.haveBuildWindow()









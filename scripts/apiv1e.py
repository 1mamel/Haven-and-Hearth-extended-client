# coding=UTF-8

__author__ = 'Vladislav Rassokhin <vladrassokhin@gmail.com>'

from apiv1 import *

def wait_context_menu():
    while not isContextMenuReady():
        sleep(100)


def wait_begin_move():
    while not isMoving(): sleep(100);


def wait_end_move():
    while isMoving(): sleep(100);


def wait_move():
    while True:
        #wait_begin_move()
        sleep(500)
        wait_end_move()
        sleep(500)
        if not isMoving():
            return


def wait_chi_cursor():
    while not is_cursor("chi"): sleep(100);


def wait_dig_cursor():
    while not is_cursor("dig"): sleep(100);


def wait_arw_cursor():
    while not is_cursor("arw"): sleep(100);


def wait_harvest_cursor():
    while not is_cursor("harvest"): sleep(100);


def wait_hourglass_on():
    while not isHourGlass(): sleep(100);


def wait_hourglass_off():
    while isHourGlass(): sleep(100);


def wait_hourglass():
    while 1:
        wait_hourglass_on()
        wait_hourglass_off()
        sleep(1000)
        if not isHourGlass():
            return


def wait_drop():
    while isDraggingItem():
        sleep(100)


def wait_drag():
    while not isDraggingItem(): sleep(100)
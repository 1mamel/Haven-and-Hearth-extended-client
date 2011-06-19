# coding=UTF-8

__author__ = 'Vladislav Rassokhin <vladrassokhin@gmail.com>'

# API version 2
#
# You can operate directly with java classes ;)
#

import haven.scriptengine.Bot as Bot
import haven.scriptengine.ScriptsManager as Manager
import haven.scriptengine.providers.Config as PConfig
import haven.scriptengine.providers.Player as PPlayer
import haven.scriptengine.providers.Util as PUtil
import haven.scriptengine.providers.UIProvider as PUI
import haven.scriptengine.providers.MapProvider as PMap
import haven.scriptengine.providers.InventoriesProvider as PInv
import haven.scriptengine.providers.CraftProvider as PCraft
import haven.scriptengine.providers.BuffsProvider as PBuffs
import haven.scriptengine.providers.CharStatsProvider as PStat


def getAttention():
    return PStat.getAttention()

def getAttentionLimit():
    return PStat.getAttentionLimit()

def getAttentionFree():
    return PStat.getAttentionLimit() - PStat.getAttention()


# returns InventoryExt
def getInventory(name):
    return PInv.getInventory(name)


def getInventoryIterator(name):
    return getInventory(name).getItemsIterator()




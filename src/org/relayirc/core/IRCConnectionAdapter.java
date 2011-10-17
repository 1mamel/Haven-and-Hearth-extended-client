//----------------------------------------------------------------------------
// $RCSfile: IRCConnectionAdapter.java,v $
// $Revision: 1.1.2.4 $
// $Author: snoopdave $
// $Date: 2001/04/08 22:44:16 $
//----------------------------------------------------------------------------

package org.relayirc.core;

import java.util.Date;

/**
 * Do-nothing implementation of IRCConnectionListener to make it easy
 * to derive new connection listeners.
 *
 * @author David M. Johnson
 *         <p/>
 *         <p>The contents of this file are subject to the Mozilla Public License
 *         Version 1.0 (the "License"); you may not use this file except in
 *         compliance with the License. You may obtain a copy of the License at
 *         http://www.mozilla.org/MPL/</p>
 *         Original Code:     Relay IRC Chat Server<br>
 *         Initial Developer: David M. Johnson <br>
 *         Contributor(s):    No contributors to this file <br>
 *         Copyright (C) 1997-2000 by David M. Johnson <br>
 *         All Rights Reserved.
 * @see IRCConnection
 * @see IRCConnectionListener
 */
public class IRCConnectionAdapter implements IRCConnectionListener {
    public void onAction(final String user, final String chan, final String txt) {
    }

    public void onBan(final String banned, final String chan, final String banner) {
    }

    public void onClientInfo(final String orgnick) {
    }

    public void onClientSource(final String orgnick) {
    }

    public void onClientVersion(final String orgnick) {
    }

    public void onConnect() {
    }

    public void onDisconnect() {
    }

    public void onIsOn(final String[] usersOn) {
    }

    public void onInvite(final String orgin, final String orgnick, final String invitee, final String chan) {
    }

    public void onJoin(final String user, final String nick, final String chan, final boolean create) {
    }

    public void onJoins(final String users, final String chan) {
    }

    public void onKick(final String kicked, final String chan, final String kicker, final String txt) {
    }

    public void onMessage(final String message) {
    }

    public void onPrivateMessage(final String orgnick, final String chan, final String txt) {
    }

    public void onNick(final String user, final String oldnick, final String newnick) {
    }

    public void onNotice(final String text) {
    }

    public void onPart(final String user, final String nick, final String chan) {
    }

    public void onOp(final String oper, final String chan, final String oped) {
    }

    public void onParsingError(final String message) {
    }

    public void onPing(final String params) {
    }

    public void onStatus(final String msg) {
    }

    public void onTopic(final String chanName, final String newTopic) {
    }

    public void onVersionNotice(final String orgnick, final String origin, final String version) {
    }

    public void onQuit(final String user, final String nick, final String txt) {
    }

    public void onReplyVersion(final String version) {
    }

    public void onReplyListUserChannels(final int channelCount) {
    }

    public void onReplyListStart() {
    }

    public void onReplyList(final String channel, final int userCount, final String topic) {
    }

    public void onReplyListEnd() {
    }

    public void onReplyListUserClient(final String msg) {
    }

    public void onReplyWhoIsUser(final String nick, final String user, final String name, final String host) {
    }

    public void onReplyWhoIsServer(final String nick, final String server, final String info) {
    }

    public void onReplyWhoIsOperator(final String info) {
    }

    public void onReplyWhoIsIdle(final String nick, final int idle, final Date signon) {
    }

    public void onReplyEndOfWhoIs(final String nick) {
    }

    public void onReplyWhoIsChannels(final String nick, final String channels) {
    }

    public void onReplyMOTDStart() {
    }

    public void onReplyMOTD(final String msg) {
    }

    public void onReplyMOTDEnd() {
    }

    public void onReplyNameReply(final String channel, final String users) {
    }

    public void onReplyTopic(final String channel, final String topic) {
    }

    public void onErrorNoMOTD() {
    }

    public void onErrorNeedMoreParams() {
    }

    public void onErrorNoNicknameGiven() {
    }

    public void onErrorNickNameInUse(final String badNick) {
    }

    public void onErrorNickCollision(final String badNick) {
    }

    public void onErrorErroneusNickname(final String badNick) {
    }

    public void onErrorAlreadyRegistered() {
    }

    public void onErrorUnknown(final String message) {
    }

    public void onErrorUnsupported(final String messag) {
    }
}

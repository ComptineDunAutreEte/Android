package fr.unice.implicitintents;

import android.content.Context;
import android.os.AsyncTask;

import model.Conversation;
import model.ConversationDataBase;
import model.MsgInfo;

public final class DecodeMessage {

    public static ConversationDataBase database;


    private DecodeMessage() {
    }

    public static void init(ConversationDataBase db) {
        database = db;
    }

    public static void decode(final String msg, final String number) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String[] lines = msg.split("\n");
                if (lines.length == 2) {
                    String[] chunks_f = lines[0].split(":");
                    String[] chunks_s = lines[1].split(":");
                    if (chunks_f.length == 2) {
                        decode(chunks_f[1], chunks_s[1], number);
                    }
                }
            }
        });

    }

    private static void decode(String code, String content, String number) {
        if (code.equals("1")) { //1 envoi
            String lines[] = content.split(";");
            String msg[] = lines[0].split("=");
            String uuid[] = lines[1].split("=");

            Conversation c = database.getConDAO().findNumber(number);

            MsgInfo m = new MsgInfo();
            m.setStatusLabel("Codé");
            m.setMessageLabel("Reçu");

            m.setContent(msg[1]);
            m.setUuid(uuid[1]);
            m.setArBut(true);
            m.setKeyBut(false);
            m.setReadBut(true);

            if (c != null) {
                m.setNumber(c.getId());
                System.out.println("Jesuis LAAAAAAAAAA");
            } else {
                c = new Conversation();
                c.setId(number);

                m.setNumber(number);

            }
            insert(c, m);

        } else if (code.equals("2")) {//sms recu
            MsgInfo m = database.getMsgInfoDAO().findMsgInfo(content);
            System.out.println(content);
            m.setMessageLabel("Envoyé");
            updateMsgInfo(m);
        } else if (code.equals("3")) {//cle recu
            MsgInfo m = database.getMsgInfoDAO().findMsgInfo(content);
            System.out.println(content);
            m.setCleLabel("Envoyé");
            m.setStatusLabel("Décodé");
            updateMsgInfo(m);
        }
    }

    public static void insertConversation(final Conversation conversation) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Conversation c = database.getConDAO().findNumber(conversation.getId());
                if (c != null) {
                    database.getConDAO().insert(conversation);
                }
            }
        });

    }

    public static void insert(final Conversation conversation, final MsgInfo msg) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Conversation c = database.getConDAO().findNumber(conversation.getId());
                if (c == null) {
                    database.getConDAO().insert(conversation);
                    database.getMsgInfoDAO().insert(msg);
                }else{
                    database.getMsgInfoDAO().insert(msg);
                }
            }
        });

    }

    public static void updateConversation(final Conversation conversation) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Conversation c = database.getConDAO().findNumber(conversation.getId());
                if (c != null) {
                    database.getConDAO().update(conversation);
                }
            }
        });
    }

    public static void deleteConversation(Conversation conversation) {
        new AsyncTask<Conversation, Void, Void>() {
            @Override
            protected Void doInBackground(Conversation... con) {
                database.getConDAO().delete(con);
                return null;
            }
        }.execute(conversation);
    }

    public static void insertMsgInfo(final MsgInfo m) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                database.getMsgInfoDAO().insert(m);
            }
        });

    }

    public static void updateMsgInfo(final MsgInfo m) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                database.getMsgInfoDAO().update(m);
            }
        });

    }

    public static void deleteMsgInfo(MsgInfo m) {
        new AsyncTask<MsgInfo, Void, Void>() {
            @Override
            protected Void doInBackground(MsgInfo... msgs) {
                database.getMsgInfoDAO().delete(msgs);
                return null;
            }
        }.execute(m);
    }


}

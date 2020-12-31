package com.infilos.hints;

import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class DictionaryTest {

    private static class User {
        String name;
        String nick;
        String mail;

        User(String name, String nick, String mail) {
            this.name = name;
            this.nick = nick;
            this.mail = mail;
        }
    }

    private static final List<User> users = new ArrayList<User>() {{
        add(new User("钟荣", "tempora", "rong.zhong@mail.com"));
        add(new User("季荣", "voluptas", "rong.ji@mail.com"));
        add(new User("谈瑜", "suscipit", "yu.tan@mail.com"));
        add(new User("熊丹", "corrupti", "dan.xiong@mail.com"));
        add(new User("熊亮", "inventore", "liang.xiong@mail.com"));
        add(new User("苗燕", "esse_sunt", "yan.miao@mail.com"));
        add(new User("苗玉", "adipisci", "yu.miao@mail.com"));
        add(new User("阳怡", "fugit", "yi.yang@mail.com"));
        add(new User("符怡", "quia", "yi.fu@mail.com"));
        add(new User("沿怡", "sapiente", "yi.yan@mail.com"));
    }};

    private static class UserDictionary extends Dictionary<User> {

        public UserDictionary(List<User> items, Configure configure) {
            super(items, configure);
        }

        @Nonnull
        @Override
        protected List<String> fields() {
            return Arrays.asList("name", "nick", "mail");
        }

        @Nullable
        @Override
        protected String extract(User item, String field) {
            if (field.equals("name")) {
                return item.name;
            }
            if (field.equals("nick")) {
                return item.nick;
            }
            if (field.equals("mail")) {
                return item.mail;
            }

            return null;
        }
    }

    private static final UserDictionary userDict = new UserDictionary(users, Configure.builder()
        .enableAllPinyin()
        .enableAllCapital()
        .build()
    );

    private static final Dictionary.StringDictionary nameDict = Dictionary.create(
        users.stream().map(u -> u.name).collect(Collectors.toList()),
        Configure.builder().enableAllPinyin().enableAllCapital().build()
    );

    @Test
    public void test() {
        userDict.hint("name", "xiong").forEach(user -> System.out.println(user.name));
    }

    @Test
    public void testObject() {
        System.out.println("--->");
        userDict.hint("name", "熊").forEach(user -> System.out.println(user.name));

        System.out.println("--->");
        userDict.hint("name", "xiong").forEach(user -> System.out.println(user.name));

        System.out.println("--->");
        userDict.hint("name", "XL").forEach(user -> System.out.println(user.name));

        System.out.println("--->");
        userDict.hint("nick", "xiong").forEach(user -> System.out.println(user.name));

        System.out.println("--->");
        userDict.hint("mail", "xiong").forEach(user -> System.out.println(user.name));
    }

    @Test
    public void testString() {
        System.out.println("--->");
        nameDict.hint("熊").forEach(System.out::println);

        System.out.println("--->");
        nameDict.hint("XL").forEach(System.out::println);

        System.out.println("--->");
        nameDict.hint("xiong").forEach(System.out::println);

        System.out.println("--->");
        nameDict.hint("怡").forEach(System.out::println);

        System.out.println("--->");
        nameDict.hint("FY").forEach(System.out::println);

        System.out.println("--->");
        nameDict.hint("yi").forEach(System.out::println);
    }
}
package com.vibely.app;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private final int BG = Color.rgb(17,17,24);
    private final int WHITE = Color.WHITE;
    private final int MUTED = Color.LTGRAY;

    private android.content.SharedPreferences prefs;
    private String name = "";
    private String username = "";
    private String bio = "";
    private final ArrayList<String> interests = new ArrayList<>();

    private static class Person {
        String name, username, interests, city;

        Person(String n, String u, String i, String c) {
            name = n;
            username = u;
            interests = i;
            city = c;
        }
    }

    private final List<Person> people = Arrays.asList(
        new Person("Maya", "@maya", "Music • Movies • Art", "Lagos"),
        new Person("Daniel", "@daniel", "Football • Gaming • Coding", "Ibadan"),
        new Person("Tobi", "@tobi", "Anime • Photography • Books", "Akure"),
        new Person("Zara", "@zara", "Travel • Dance • Comedy", "Benin City"),
        new Person("Chris", "@chris", "Technology • Sports • Movies", "Abeokuta")
    );

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        prefs = getSharedPreferences("vibely", Context.MODE_PRIVATE);
        load();

        if (prefs.getBoolean("logged", false)) {
            home();
        } else {
            welcome();
        }
    }

    private void load() {
        name = prefs.getString("name", "");
        username = prefs.getString("user", "");
        bio = prefs.getString("bio", "");

        interests.clear();

        String saved = prefs.getString("ints", "");

        if (saved != null && !saved.isEmpty()) {
            interests.addAll(Arrays.asList(saved.split("\\|")));
        }
    }

    private void save() {
        prefs.edit()
            .putString("name", name)
            .putString("user", username)
            .putString("bio", bio)
            .putString("ints", joinInterests())
            .putBoolean("logged", true)
            .apply();
    }

    private String joinInterests() {
        StringBuilder s = new StringBuilder();

        for (int i = 0; i < interests.size(); i++) {
            if (i > 0) {
                s.append("|");
            }

            s.append(interests.get(i));
        }

        return s.toString();
    }

    private LinearLayout root(String title, final Runnable back) {
        LinearLayout r = new LinearLayout(this);

        r.setOrientation(LinearLayout.VERTICAL);
        r.setPadding(20, 24, 20, 12);
        r.setBackgroundColor(BG);

        LinearLayout bar = new LinearLayout(this);
        bar.setGravity(Gravity.CENTER_VERTICAL);

        if (back != null) {
            Button b = button("‹", back);
            bar.addView(b, new LinearLayout.LayoutParams(55, 55));
        }

        TextView t = text(title, 27);
        t.setTypeface(Typeface.DEFAULT_BOLD);

        bar.addView(
            t,
            new LinearLayout.LayoutParams(0, 60, 1f)
        );

        r.addView(bar);

        setContentView(r);

        return r;
    }

    private TextView text(String s, float size) {
        TextView t = new TextView(this);

        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(WHITE);
        t.setPadding(4, 10, 4, 10);

        return t;
    }

    private Button button(String s, final Runnable action) {
        Button b = new Button(this);

        b.setText(s);
        b.setOnClickListener(v -> action.run());

        return b;
    }

    private EditText field(String hint, String value) {
        EditText e = new EditText(this);

        e.setHint(hint);
        e.setText(value);
        e.setTextColor(WHITE);
        e.setHintTextColor(MUTED);
        e.setSingleLine(true);

        return e;
    }

    private void welcome() {
        LinearLayout r = root("", null);

        TextView logo = text("Vibely", 40);
        logo.setGravity(Gravity.CENTER);

        r.addView(
            logo,
            new LinearLayout.LayoutParams(-1, 110)
        );

        TextView sub = text(
            "Connect through shared interests.",
            18
        );

        sub.setGravity(Gravity.CENTER);
        r.addView(sub);

        r.addView(
            button("Create account", this::signup)
        );

        r.addView(
            button("Log in", this::login)
        );
    }

    private void signup() {
        LinearLayout r = root(
            "Create account",
            this::welcome
        );

        EditText n = field("Display name", "");
        EditText u = field("Username", "");
        EditText e = field("Email", "");
        EditText p = field("Password", "");

        r.addView(
            n,
            new LinearLayout.LayoutParams(-1, 60)
        );

        r.addView(
            u,
            new LinearLayout.LayoutParams(-1, 60)
        );

        r.addView(
            e,
            new LinearLayout.LayoutParams(-1, 60)
        );

        r.addView(
            p,
            new LinearLayout.LayoutParams(-1, 60)
        );

        r.addView(
            button("Create account", () -> {

                name = n.getText().toString().trim();
                username = u.getText().toString().trim();

                if (name.isEmpty()) {
                    name = "Vibely User";
                }

                if (username.isEmpty()) {
                    username = "@user";
                }

                if (!username.startsWith("@")) {
                    username = "@" + username;
                }

                bio = "New to Vibely 👋";
                interests.clear();

                save();
                interests();
            })
        );

        r.addView(
            button(
                "Already have an account? Log in",
                this::login
            )
        );
    }

    private void login() {
        LinearLayout r = root(
            "Log in",
            this::welcome
        );

        EditText e = field(
            "Email or username",
            ""
        );

        EditText p = field(
            "Password",
            ""
        );

        r.addView(
            e,
            new LinearLayout.LayoutParams(-1, 60)
        );

        r.addView(
            p,
            new LinearLayout.LayoutParams(-1, 60)
        );

        r.addView(
            button("Log in", () -> {

                load();

                if (name.isEmpty()) {
                    name = "Vibely User";
                    username = "@user";
                    bio = "Welcome to Vibely";

                    save();
                } else {
                    prefs.edit()
                        .putBoolean("logged", true)
                        .apply();
                }

                home();
            })
        );

        r.addView(
            text(
                "Demo build: account data is stored on this phone.",
                14
            )
        );
    }

    private void home() {
        LinearLayout r = root(
            "Vibely",
            null
        );

        EditText q = field(
            "🔎  Search people by name or username",
            ""
        );

        r.addView(
            q,
            new LinearLayout.LayoutParams(-1, 62)
        );

        r.addView(
            button(
                "Search",
                () -> search(q.getText().toString())
            )
        );

        r.addView(
            text("Discover", 22)
        );

        for (int i = 0; i < 3; i++) {
            r.addView(
                personButton(people.get(i))
            );
        }

        r.addView(
            button(
                "See all people",
                this::discover
            )
        );

        r.addView(
            text("Quick access", 22)
        );

        r.addView(
            button("👤 My Profile", this::profile)
        );

        r.addView(
            button("🤝 Connections", this::connections)
        );

        r.addView(
            button("💬 Messages", this::messages)
        );
    }

    private Button personButton(final Person p) {
        return button(
            "👤 " + p.name +
            "\n" +
            p.username +
            "  •  " +
            p.interests,

            () -> personPage(p)
        );
    }

    private void search(String query) {
        LinearLayout r = root(
            "Search",
            this::home
        );

        String q = query
            .trim()
            .toLowerCase(Locale.getDefault());

        r.addView(
            text(
                q.isEmpty()
                    ? "People on Vibely"
                    : "Results for \"" + query + "\"",
                18
            )
        );

        int count = 0;

        for (Person p : people) {

            if (
                q.isEmpty()
                ||
                p.name
                    .toLowerCase(Locale.getDefault())
                    .contains(q)
                ||
                p.username
                    .toLowerCase(Locale.getDefault())
                    .contains(q)
            ) {

                r.addView(
                    personButton(p)
                );

                count++;
            }
        }

        if (count == 0) {
            r.addView(
                text(
                    "No matching people found.",
                    16
                )
            );
        }
    }

    private void discover() {
        LinearLayout r = root(
            "Discover",
            this::home
        );

        r.addView(
            text(
                "People you might connect with",
                18
            )
        );

        for (Person p : people) {
            r.addView(
                personButton(p)
            );
        }
    }

    private void personPage(final Person p) {
        LinearLayout r = root(
            "Profile",
            this::home
        );

        r.addView(
            text("👤 " + p.name, 30)
        );

        r.addView(
            text(p.username, 18)
        );

        r.addView(
            text("📍 " + p.city, 15)
        );

        r.addView(
            text(
                "Interests\n" + p.interests,
                17
            )
        );

        r.addView(
            button(
                "Connect",
                () -> Toast.makeText(
                    this,
                    "Connection request sent",
                    Toast.LENGTH_SHORT
                ).show()
            )
        );

        r.addView(
            button(
                "Report / Block",
                () -> Toast.makeText(
                    this,
                    "Safety options opened",
                    Toast.LENGTH_SHORT
                ).show()
            )
        );
    }

    private void profile() {
        load();

        LinearLayout r = root(
            "My Profile",
            this::home
        );

        r.addView(
            text(
                "👤 " +
                (
                    name.isEmpty()
                        ? "Your Name"
                        : name
                ),
                30
            )
        );

        r.addView(
            text(
                username.isEmpty()
                    ? "@username"
                    : username,
                18
            )
        );

        r.addView(
            text(
                bio.isEmpty()
                    ? "Tell people about yourself."
                    : bio,
                17
            )
        );

        r.addView(
            text("Interests", 20)
        );

        r.addView(
            text(
                interests.isEmpty()
                    ? "No interests selected yet."
                    : joinForDisplay(),
                16
            )
        );

        r.addView(
            button(
                "✏️ Edit Profile",
                this::edit
            )
        );

        r.addView(
            button(
                "⭐ Choose Interests",
                this::interests
            )
        );

        r.addView(
            button(
                "Log out",
                () -> {
                    prefs.edit()
                        .putBoolean("logged", false)
                        .apply();

                    welcome();
                }
            )
        );
    }

    private String joinForDisplay() {
        return String.join(
            " • ",
            interests
        );
    }

    private void edit() {
        LinearLayout r = root(
            "Edit Profile",
            this::profile
        );

        EditText n = field(
            "Display name",
            name
        );

        EditText u = field(
            "Username",
            username
        );

        EditText b = field(
            "Bio",
            bio
        );

        r.addView(
            n,
            new LinearLayout.LayoutParams(-1, 65)
        );

        r.addView(
            u,
            new LinearLayout.LayoutParams(-1, 65)
        );

        r.addView(
            b,
            new LinearLayout.LayoutParams(-1, 65)
        );

        r.addView(
            button(
                "Save changes",
                () -> {

                    name = n
                        .getText()
                        .toString()
                        .trim();

                    username = u
                        .getText()
                        .toString()
                        .trim();

                    bio = b
                        .getText()
                        .toString()
                        .trim();

                    if (name.isEmpty()) {
                        name = "Vibely User";
                    }

                    if (username.isEmpty()) {
                        username = "@user";
                    }

                    if (!username.startsWith("@")) {
                        username = "@" + username;
                    }

                    if (bio.isEmpty()) {
                        bio = "Tell people about yourself.";
                    }

                    save();
                    profile();
                }
            )
        );
    }

    private void interests() {
        LinearLayout r = root(
            "Your Interests",
            this::profile
        );

        r.addView(
            text(
                "Pick up to 8 interests.",
                17
            )
        );

        String[] choices = {
            "Gaming",
            "Music",
            "Sports",
            "Movies",
            "Books",
            "Art",
            "Technology",
            "Comedy",
            "Travel",
            "Photography",
            "Cooking",
            "Fashion",
            "Fitness",
            "Anime",
            "Football",
            "Basketball",
            "Coding",
            "Nature",
            "Dance",
            "Writing"
        };

        for (String c : choices) {

            final String choice = c;

            CheckBox cb = new CheckBox(this);

            cb.setText(choice);
            cb.setTextSize(16);
            cb.setTextColor(WHITE);
            cb.setChecked(
                interests.contains(choice)
            );

            cb.setOnCheckedChangeListener(
                (buttonView, checked) -> {

                    if (
                        checked
                        &&
                        !interests.contains(choice)
                    ) {

                        if (interests.size() < 8) {

                            interests.add(choice);

                        } else {

                            cb.setChecked(false);

                            Toast.makeText(
                                this,
                                "Maximum 8 interests",
                                Toast.LENGTH_SHORT
                            ).show();
                        }

                    } else if (!checked) {

                        interests.remove(choice);
                    }
                }
            );

            r.addView(cb);
        }

        r.addView(
            button(
                "Save interests",
                () -> {
                    save();
                    profile();
                }
            )
        );
    }

    private void connections() {
        LinearLayout r = root(
            "Connections",
            this::home
        );

        r.addView(
            text(
                "No connections yet.",
                18
            )
        );

        r.addView(
            button(
                "Find people",
                this::discover
            )
        );
    }

    private void messages() {
        LinearLayout r = root(
            "Messages",
            this::home
        );

        r.addView(
            text(
                "No messages yet.",
                18
            )
        );

        r.addView(
            text(
                "Your conversations will appear here.",
                16
            )
        );
    }
}

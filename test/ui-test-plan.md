# Dobby UI Test Plan

## Test configuration

- Application: `dobby.Dobby`.
- Java version: 25.
- Compile command: `javac -d <temporary-build-dir> src\main\java\dobby\Task.java src\main\java\dobby\Dobby.java`.
- Run command: `java -cp <temporary-build-dir> dobby.Dobby`.
- Input format: one command per line, in the order shown in each test case.
- Typed task syntax: `todo <description>`, `deadline <description> /by <date/time>`, and `event <description> /from <start> /to <end>`.
- Date/time text is kept as entered, so natural values such as `Sunday`, `Mon 2pm`, and `11/10/2019 5pm` are valid.
- Comparison: exact stdout, ignoring only CRLF versus LF line endings and a final newline.
- Execution order: top to bottom; stop immediately after the first failure.
- Session record: print the complete console input and output for every executed case.

## Test cases

### TC-001 — List tasks when the task pouch is empty

- Aim: Verify that `list` reports an empty task list in a new session.
- Inputs (one command per line):

```text
list
bye
```

- Expected output:

```text
____________________________________________________________
     *        .        *        .        *
      ____          _      _
     |  _ \   ___  | |__  | |__   _   _
     | | | | / _ \ | '_ \ | '_ \ | | | |
     | |_| || (_) || |_) || |_) || |_| |
     |____/  \___/ |_.__/ |_.__/  \__, |
                                  |___/
     .        *        .        *        .
Hello! I'm Dobby, your mildly magical command goblin.
What adventure shall we get into today?
    ____________________________________________________________
     Behold, brave adventurer! Here are your mighty quests:
     Your task pouch is empty - add a quest and let the adventure begin!
    ____________________________________________________________
    ____________________________________________________________
     Bye for now! Dobby is off to polish the quest scrolls. Stay mighty!
    ____________________________________________________________
```

### TC-002 — Add a task and list it

- Aim: Verify that a normal input is added and appears as an unfinished task.
- Inputs (one command per line):

```text
finish homework
list
bye
```

- Expected output:

```text
____________________________________________________________
     *        .        *        .        *
      ____          _      _
     |  _ \   ___  | |__  | |__   _   _
     | | | | / _ \ | '_ \ | '_ \ | | | |
     | |_| || (_) || |_) || |_) || |_| |
     |____/  \___/ |_.__/ |_.__/  \__, |
                                  |___/
     .        *        .        *        .
Hello! I'm Dobby, your mildly magical command goblin.
What adventure shall we get into today?
    ____________________________________________________________
     Quest accepted! Added to your magical task scroll: finish homework
    ____________________________________________________________
    ____________________________________________________________
     Behold, brave adventurer! Here are your mighty quests:
     1.[T][ ] finish homework
    ____________________________________________________________
    ____________________________________________________________
     Bye for now! Dobby is off to polish the quest scrolls. Stay mighty!
    ____________________________________________________________
```

### TC-003 — Mark and unmark a task

- Aim: Verify that `mark` changes the task status to done and `unmark` restores the unfinished status.
- Inputs (one command per line):

```text
pack lunch
mark 1
list
unmark 1
list
bye
```

- Expected output:

```text
____________________________________________________________
     *        .        *        .        *
      ____          _      _
     |  _ \   ___  | |__  | |__   _   _
     | | | | / _ \ | '_ \ | '_ \ | | | |
     | |_| || (_) || |_) || |_) || |_| |
     |____/  \___/ |_.__/ |_.__/  \__, |
                                  |___/
     .        *        .        *        .
Hello! I'm Dobby, your mildly magical command goblin.
What adventure shall we get into today?
    ____________________________________________________________
     Quest accepted! Added to your magical task scroll: pack lunch
    ____________________________________________________________
    ____________________________________________________________
     Nice! Quest progress unlocked - I've marked this task as done:
       [T][X] pack lunch
    ____________________________________________________________
    ____________________________________________________________
     Behold, brave adventurer! Here are your mighty quests:
     1.[T][X] pack lunch
    ____________________________________________________________
    ____________________________________________________________
     Plot twist! This quest is back on the adventure board -
     I've marked this task as not done yet:
       [T][ ] pack lunch
    ____________________________________________________________
    ____________________________________________________________
     Behold, brave adventurer! Here are your mighty quests:
     1.[T][ ] pack lunch
    ____________________________________________________________
    ____________________________________________________________
     Bye for now! Dobby is off to polish the quest scrolls. Stay mighty!
    ____________________________________________________________
```

### TC-004 — Reject an invalid mark command

- Aim: Verify that a non-numeric task selector produces a helpful error and does not terminate the session.
- Inputs (one command per line):

```text
mark nope
bye
```

- Expected output:

```text
____________________________________________________________
     *        .        *        .        *
      ____          _      _
     |  _ \   ___  | |__  | |__   _   _
     | | | | / _ \ | '_ \ | '_ \ | | | |
     | |_| || (_) || |_) || |_) || |_| |
     |____/  \___/ |_.__/ |_.__/  \__, |
                                  |___/
     .        *        .        *        .
Hello! I'm Dobby, your mildly magical command goblin.
What adventure shall we get into today?
    ____________________________________________________________
     Please tell me which task number to mark - Dobby cannot read that quest rune!
    ____________________________________________________________
    ____________________________________________________________
     Bye for now! Dobby is off to polish the quest scrolls. Stay mighty!
    ____________________________________________________________
```

### TC-005 — Add ToDos, Deadlines, and Events

- Aim: Verify that all three explicit task commands are accepted, retain their date/time text, and display distinct type markers.
- Inputs (one command per line):

```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

- Expected output:

```text
____________________________________________________________
     *        .        *        .        *
      ____          _      _
     |  _ \   ___  | |__  | |__   _   _
     | | | | / _ \ | '_ \ | '_ \ | | | |
     | |_| || (_) || |_) || |_) || |_| |
     |____/  \___/ |_.__/ |_.__/  \__, |
                                  |___/
     .        *        .        *        .
Hello! I'm Dobby, your mildly magical command goblin.
What adventure shall we get into today?
    ____________________________________________________________
     Huzzah! A new quest has joined your magical task scroll:
       [T][ ] borrow book
     The quest scroll now holds 1 quest. Keep adventuring!
    ____________________________________________________________
    ____________________________________________________________
     Huzzah! A new quest has joined your magical task scroll:
       [D][ ] return book (by: Sunday)
     The quest scroll now holds 2 quests. Keep adventuring!
    ____________________________________________________________
    ____________________________________________________________
     Huzzah! A new quest has joined your magical task scroll:
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
     The quest scroll now holds 3 quests. Keep adventuring!
    ____________________________________________________________
    ____________________________________________________________
     Behold, brave adventurer! Here are your mighty quests:
     1.[T][ ] borrow book
     2.[D][ ] return book (by: Sunday)
     3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
    ____________________________________________________________
    ____________________________________________________________
     Bye for now! Dobby is off to polish the quest scrolls. Stay mighty!
    ____________________________________________________________
```

### TC-006 — Preserve numeric date and time ranges

- Aim: Verify that numeric dates and date ranges remain intact in Deadline and Event displays.
- Inputs (one command per line):

```text
deadline submit report /by 11/10/2019 5pm
event orientation week /from 4/10/2019 /to 11/10/2019
list
bye
```

- Expected output:

```text
____________________________________________________________
     *        .        *        .        *
      ____          _      _
     |  _ \   ___  | |__  | |__   _   _
     | | | | / _ \ | '_ \ | '_ \ | | | |
     | |_| || (_) || |_) || |_) || |_| |
     |____/  \___/ |_.__/ |_.__/  \__, |
                                  |___/
     .        *        .        *        .
Hello! I'm Dobby, your mildly magical command goblin.
What adventure shall we get into today?
    ____________________________________________________________
     Huzzah! A new quest has joined your magical task scroll:
       [D][ ] submit report (by: 11/10/2019 5pm)
     The quest scroll now holds 1 quest. Keep adventuring!
    ____________________________________________________________
    ____________________________________________________________
     Huzzah! A new quest has joined your magical task scroll:
       [E][ ] orientation week (from: 4/10/2019 to: 11/10/2019)
     The quest scroll now holds 2 quests. Keep adventuring!
    ____________________________________________________________
    ____________________________________________________________
     Behold, brave adventurer! Here are your mighty quests:
     1.[D][ ] submit report (by: 11/10/2019 5pm)
     2.[E][ ] orientation week (from: 4/10/2019 to: 11/10/2019)
    ____________________________________________________________
    ____________________________________________________________
     Bye for now! Dobby is off to polish the quest scrolls. Stay mighty!
    ____________________________________________________________
```

### TC-007 — Accept an arbitrary deadline description

- Aim: Verify that deadline text is treated as a string and does not need to be a recognized date or time.
- Inputs (one command per line):

```text
deadline do homework /by no idea :-p
bye
```

- Expected output:

```text
____________________________________________________________
     *        .        *        .        *
      ____          _      _
     |  _ \   ___  | |__  | |__   _   _
     | | | | / _ \ | '_ \ | '_ \ | | | |
     | |_| || (_) || |_) || |_) || |_| |
     |____/  \___/ |_.__/ |_.__/  \__, |
                                  |___/
     .        *        .        *        .
Hello! I'm Dobby, your mildly magical command goblin.
What adventure shall we get into today?
    ____________________________________________________________
     Huzzah! A new quest has joined your magical task scroll:
       [D][ ] do homework (by: no idea :-p)
     The quest scroll now holds 1 quest. Keep adventuring!
    ____________________________________________________________
    ____________________________________________________________
     Bye for now! Dobby is off to polish the quest scrolls. Stay mighty!
    ____________________________________________________________
```

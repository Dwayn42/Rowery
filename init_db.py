import sqlite3, os

DB = 'bike_rental.db'
if os.path.exists(DB):
    print("DB already exists")
else:
    conn = sqlite3.connect(DB)
    cur = conn.cursor()
    cur.executescript(open('schema.sql', encoding='utf-8').read())
    cur.executescript(open('data/seed.sql', encoding='utf-8').read())
    conn.commit()
    conn.close()
    print("DB initialized and seeded.")

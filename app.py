from flask import Flask, render_template, request, redirect, url_for, flash, session, g
import sqlite3
from werkzeug.security import generate_password_hash, check_password_hash
from datetime import datetime
import os

DATABASE = 'bike_rental.db'
app = Flask(__name__)
app.secret_key = 'dev-secret-key-change-this'

def get_db():
    db = getattr(g, '_database', None)
    if db is None:
        db = g._database = sqlite3.connect(DATABASE)
        db.row_factory = sqlite3.Row
    return db

@app.teardown_appcontext
def close_connection(exception):
    db = getattr(g, '_database', None)
    if db is not None:
        db.close()

def init_db():
    db = sqlite3.connect(DATABASE)
    cur = db.cursor()
    cur.executescript(open('schema.sql', encoding='utf-8').read())
    db.commit()
    db.close()

@app.route('/')
def index():
    db = get_db()
    bikes = db.execute("SELECT * FROM bikes WHERE available=1").fetchall()
    return render_template('index.html', bikes=bikes)

@app.route('/register', methods=['GET','POST'])
def register():
    if request.method=='POST':
        username = request.form['username']
        email = request.form['email']
        password = request.form['password']
        db = get_db()
        existing = db.execute("SELECT id FROM users WHERE username=?", (username,)).fetchone()
        if existing:
            flash('Nazwa użytkownika zajęta', 'danger')
            return redirect(url_for('register'))
        pw_hash = generate_password_hash(password)
        db.execute("INSERT INTO users (username, email, password_hash) VALUES (?,?,?)",
                   (username,email,pw_hash))
        db.commit()
        flash('Zarejestrowano. Zaloguj się.', 'success')
        return redirect(url_for('login'))
    return render_template('register.html')

@app.route('/login', methods=['GET','POST'])
def login():
    if request.method=='POST':
        username = request.form['username']
        password = request.form['password']
        db = get_db()
        user = db.execute("SELECT * FROM users WHERE username=?", (username,)).fetchone()
        if user and check_password_hash(user['password_hash'], password):
            session['user_id'] = user['id']
            session['username'] = user['username']
            flash('Zalogowano', 'success')
            return redirect(url_for('index'))
        flash('Błędne dane logowania', 'danger')
    return render_template('login.html')

@app.route('/logout')
def logout():
    session.clear()
    flash('Wylogowano', 'info')
    return redirect(url_for('index'))

@app.route('/bike/<int:bike_id>')
def bike_detail(bike_id):
    db = get_db()
    bike = db.execute("SELECT * FROM bikes WHERE id=?", (bike_id,)).fetchone()
    return render_template('bike.html', bike=bike)

@app.route('/reserve/<int:bike_id>', methods=['GET','POST'])
def reserve(bike_id):
    if 'user_id' not in session:
        flash('Zaloguj się, aby zarezerwować rower', 'warning')
        return redirect(url_for('login'))
    db = get_db()
    bike = db.execute("SELECT * FROM bikes WHERE id=?", (bike_id,)).fetchone()
    if request.method=='POST':
        start = request.form['start_date']
        end = request.form['end_date']
        created = datetime.utcnow().isoformat()
        db.execute("INSERT INTO reservations (user_id, bike_id, start_date, end_date, created_at) VALUES (?,?,?,?,?)",
                   (session['user_id'], bike_id, start, end, created))
        db.execute("UPDATE bikes SET available=0 WHERE id=?", (bike_id,))
        db.commit()
        flash('Rezerwacja zakończona sukcesem', 'success')
        return redirect(url_for('index'))
    return render_template('reserve.html', bike=bike)

@app.route('/admin')
def admin():
    db = get_db()
    bikes = db.execute("SELECT * FROM bikes").fetchall()
    reservations = db.execute("""SELECT r.id, r.start_date, r.end_date, r.created_at,
                                        u.username, b.model FROM reservations r
                                        JOIN users u ON r.user_id=u.id
                                        JOIN bikes b ON r.bike_id=b.id
                                     ORDER BY r.created_at DESC""").fetchall()
    return render_template('admin.html', bikes=bikes, reservations=reservations)

@app.route('/admin/add_bike', methods=['GET','POST'])
def add_bike():
    if request.method=='POST':
        model = request.form['model']
        description = request.form['description']
        price = request.form['price']
        db = get_db()
        db.execute("INSERT INTO bikes (model, description, price_per_day, available) VALUES (?,?,?,1)",
                   (model,description,price))
        db.commit()
        flash('Dodano rower', 'success')
        return redirect(url_for('admin'))
    return render_template('add_bike.html')

if __name__=='__main__':
    if not os.path.exists(DATABASE):
        init_db()
        print('DB initialized')
    app.run(host='0.0.0.0', port=5000, debug=True)

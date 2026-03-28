import psycopg2

try:
    conn = psycopg2.connect("dbname=zenith_flow user=postgres password=password host=localhost port=5432")
    cur = conn.cursor()
    cur.execute("SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'")
    tables = cur.fetchall()
    print("Tables in zenith_flow (public):")
    for table in tables:
        print(f" - {table[0]}")
except Exception as e:
    print(f"Error: {e}")


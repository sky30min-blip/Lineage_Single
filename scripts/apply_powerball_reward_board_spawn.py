import pymysql


def main():
    con = pymysql.connect(
        host="localhost",
        port=3306,
        user="root",
        password="1307",
        database="lin200",
        charset="utf8mb4",
    )
    cur = con.cursor()
    try:
        cur.execute("DELETE FROM background_spawnlist WHERE name='powerball_reward_board'")
        inserted = cur.execute(
            """
            INSERT INTO background_spawnlist
            (name,nameid,gfx,gfx_mode,lawful,light,title,locX,locY,locMap,locSize,heading,item_nameid,item_count,item_remove)
            SELECT
              'powerball_reward_board','게시판',gfx,gfx_mode,lawful,light,'powerball_reward',33417,32821,4,
              locSize,heading,item_nameid,item_count,item_remove
            FROM background_spawnlist
            WHERE title='server'
            LIMIT 1
            """
        )
        # server 템플릿이 없는 서버팩 대비 하드코딩 폴백
        if inserted == 0:
            cur.execute(
                """
                INSERT INTO background_spawnlist
                (name,nameid,gfx,gfx_mode,lawful,light,title,locX,locY,locMap,locSize,heading,item_nameid,item_count,item_remove)
                VALUES
                ('powerball_reward_board','게시판',1546,0,0,0,'powerball_reward',33417,32821,4,0,0,0,0,'false')
                """
            )
        con.commit()

        # 서버가 실행 중이면 배경 리로드 큐를 통해 즉시 반영
        cur.execute("INSERT INTO gm_server_command (command, param, executed) VALUES ('reload', 'background_spawnlist', 0)")
        con.commit()
        print("OK: powerball_reward_board inserted, reload queued(background_spawnlist)")
    finally:
        cur.close()
        con.close()


if __name__ == "__main__":
    main()

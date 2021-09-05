import time

from user import User

import psycopg2

from config import read_config
from messages import *

POSTGRESQL_CONFIG_FILE_NAME = "database.cfg"

"""
    Connects to PostgreSQL database and returns connection object.
"""


def connect_to_db():
    db_conn_params = read_config(filename=POSTGRESQL_CONFIG_FILE_NAME, section="postgresql")
    conn = psycopg2.connect(**db_conn_params)
    conn.autocommit = False
    return conn


"""
    Splits given command string by spaces and trims each token.
    Returns token list.
"""

start = 0
end = 0


def tokenize_command(command):
    tokens = command.split(" ")
    return [t.strip() for t in tokens]


"""
    Prints list of available commands of the software.
"""


def help(conn, user):
    print("\n*** Please enter one of the following commands ***")
    print("> help")
    if user is not None:
        print("> sign_out")
        print("> show_memberships")
        print("> show_subscription")
        print("> subscribe <membership_id>")
        print("> review <review_id> <business_id> <stars>")
        print("> search_for_businesses <keyword_1> <keyword_2> <keyword_3> ... <keyword_n>")
        cursor = conn.cursor()
        user_id = user.user_id
        premium_user_query = "select * from subscription where user_id = (%s)"
        cursor.execute(premium_user_query, (user_id,))
        premium_user = cursor.fetchone()
        if premium_user is not None:
            print("> suggest_businesses")
            print("> get_coupon")
    elif user is None:
        print("> sign_up <user_id> <first_name> <last_name>")
        print("> sign_in <user_id>")
        print("> quit")


"""
    Saves user with given details.
    - Return type is a tuple, 1st element is a boolean and 2nd element is the response message from messages.py.
    - If the operation is successful, commit changes and return tuple (True, CMD_EXECUTION_SUCCESS).
    - If any exception occurs; rollback, do nothing on the database and return tuple (False, CMD_EXECUTION_FAILED).
"""


def sign_up(conn, user_id, user_name):
    conn.autocommit = False
    cursor = conn.cursor()
    try:
        query = "INSERT INTO users(user_id, user_name) VALUES(%s, %s);"
        cursor.execute(query, (user_id, user_name))
        conn.commit()
        return True, CMD_EXECUTION_SUCCESS
    except (Exception, psycopg2.DatabaseError):
        conn.rollback()
        return False, CMD_EXECUTION_FAILED


"""
    Retrieves user information if there is a user with given user_id and user's session_count < max_parallel_sessions.
    - Return type is a tuple, 1st element is a user object and 2nd element is the response message from messages.py.
    - If there is no such user, return tuple (None, USER_SIGNIN_FAILED).
    - If session_count < max_parallel_sessions, commit changes (increment session_count) and return tuple (user, CMD_EXECUTION_SUCCESS).
    - If session_count >= max_parallel_sessions, return tuple (None, USER_ALL_SESSIONS_ARE_USED).
    - If any exception occurs; rollback, do nothing on the database and return tuple (None, USER_SIGNIN_FAILED).
"""


def sign_in(conn, user_id):
    cursor = conn.cursor()
    global start
    try:
        user_exists_query = "select * from users where user_id = (%s);"
        cursor.execute(user_exists_query, (user_id,))
        user = cursor.fetchone()
        if user is not None:
            session_count = user[9]
            user_subscription = "select * from subscription where user_id = (%s)"
            cursor.execute(user_subscription, (user_id,))
            membership_id = cursor.fetchone()
            if membership_id is None:
                if session_count < 1:
                    increment_session_count_query = "update users set session_count = session_count + 1 where user_id = (%s);"
                    cursor.execute(increment_session_count_query, (user_id,))
                    conn.commit()
                    start = time.time()
                    return User(user[0], user[1], user[2], user[3], user[4], user[5], user[6], user[7], user[8],
                                user[9]), CMD_EXECUTION_SUCCESS
                else:
                    return None, USER_ALL_SESSIONS_ARE_USED
            else:
                membership_max_parallel_sessions_query = "select * from membership where membership_id = (%s)"
                cursor.execute(membership_max_parallel_sessions_query, (membership_id[1],))
                max_parallel_sessions = cursor.fetchone()
                if session_count >= max_parallel_sessions[2]:
                    return None, USER_ALL_SESSIONS_ARE_USED
                else:
                    increment_session_count_query = "update users set session_count = session_count + 1 where user_id = (%s);"
                    cursor.execute(increment_session_count_query, (user_id,))
                    conn.commit()
                    start = time.time()
                    return User(user[0], user[1], user[2], user[3], user[4], user[5], user[6], user[7], user[8],
                                user[9]), CMD_EXECUTION_SUCCESS
        else:
            return None, USER_SIGNIN_FAILED
    except (Exception, psycopg2.DatabaseError):
        conn.rollback()
        return None, USER_SIGNIN_FAILED


"""
    Signs out from given user's account.
    - Return type is a tuple, 1st element is a boolean and 2nd element is the response message from messages.py.
    - Decrement session_count of the user in the database.
    - If the operation is successful, commit changes and return tuple (True, CMD_EXECUTION_SUCCESS).
    - If any exception occurs; rollback, do nothing on the database and return tuple (False, CMD_EXECUTION_FAILED).
"""


def sign_out(conn, user):
    cursor = conn.cursor()
    global end
    try:
        user_id = user.user_id
        session_count_query = "select * from users where user_id = (%s);"
        cursor.execute(session_count_query, (user_id,))
        session_count = cursor.fetchone()
        if session_count is not None:
            if session_count[9] > 0:
                user_sign_out_query = "update users set session_count = session_count - 1 where user_id = (%s)"
                cursor.execute(user_sign_out_query, (user.user_id,))
                end = time.time()
                spend_time = round(((end - start) * 1000), 2)
                update_subscription_query = "update subscription set time_spent = time_spent + (%s) where user_id = (%s)"
                cursor.execute(update_subscription_query, (spend_time, user.user_id))
                conn.commit()
                return True, CMD_EXECUTION_SUCCESS
            else:
                return False, CMD_EXECUTION_FAILED
        else:
            return False, CMD_EXECUTION_FAILED
    except (Exception, psycopg2.DatabaseError):
        conn.rollback()
        return False, CMD_EXECUTION_FAILED


"""
    Quits from program.
    - Return type is a tuple, 1st element is a boolean and 2nd element is the response message from messages.py.
    - Remember to sign authenticated user out first.
    - If the operation is successful, commit changes and return tuple (True, CMD_EXECUTION_SUCCESS).
    - If any exception occurs; rollback, do nothing on the database and return tuple (False, CMD_EXECUTION_FAILED).
"""


def quit(conn, user):
    try:
        if user is None:
            return True, CMD_EXECUTION_SUCCESS
        else:
            return sign_out(conn, user)
    except (Exception, psycopg2.DatabaseError):
        return False, CMD_EXECUTION_FAILED


"""
    Retrieves all available memberships and prints them.
    - Return type is a tuple, 1st element is a boolean and 2nd element is the response message from messages.py.
    - If the operation is successful; print available memberships and return tuple (True, CMD_EXECUTION_SUCCESS).
    - If any exception occurs; return tuple (False, CMD_EXECUTION_FAILED).

    Output should be like:
    #|Name|Max Sessions|Monthly Fee
    1|Silver|2|30
    2|Gold|4|50
    3|Platinum|10|90
"""


def show_memberships(conn, user):
    cursor = conn.cursor()
    try:
        get_membership_query = "select * from membership;"
        cursor.execute(get_membership_query)
        membership_list = cursor.fetchall()
        print('#|Name|Max Sessions|Monthly Fee')
        for i in membership_list:
            print("%d|%s|%s|%s" % (i[0], i[1], i[2], i[3]))
        return True, CMD_EXECUTION_SUCCESS
    except (Exception, psycopg2.DatabaseError):
        return False, CMD_EXECUTION_FAILED


"""
    Retrieves authenticated user's membership and prints it. 
    - Return type is a tuple, 1st element is a boolean and 2nd element is the response message from messages.py.
    - If the operation is successful; print the authenticated user's membership and return tuple (True, CMD_EXECUTION_SUCCESS).
    - If any exception occurs; return tuple (False, CMD_EXECUTION_FAILED).

    Output should be like:
    #|Name|Max Sessions|Monthly Fee
    2|Gold|4|50
"""


def show_subscription(conn, user):
    cursor = conn.cursor()
    try:
        subscription_query = "select * from subscription where user_id = (%s)"
        user_id = user.user_id
        cursor.execute(subscription_query, (user_id,))
        membership_info = cursor.fetchone()
        if membership_info is None:
            return False, CMD_EXECUTION_FAILED
        else:
            membership_id = str(membership_info[1])
            membership_query = "select * from membership where membership_id = (%s)"
            cursor.execute(membership_query, (membership_id,))
            membership = cursor.fetchone()
            print('#|Name|Max Sessions|Monthly Fee')
            print("%d|%s|%s|%s" % (membership[0], membership[1], membership[2], membership[3]))
            return True, CMD_EXECUTION_SUCCESS
    except (Exception, psycopg2.DatabaseError):
        return False, CMD_EXECUTION_FAILED


"""
    Insert user-review-business relationship to Review table if not exists in Review table.
    - Return type is a tuple, 1st element is a boolean and 2nd element is the response message from messages.py.
    - If a user-review-business relationship already exists (checking review_id is enough), do nothing on the database and return (True, CMD_EXECUTION_SUCCESS).
    - If the operation is successful, commit changes and return tuple (True, CMD_EXECUTION_SUCCESS).
    - If the business_id is incorrect; rollback, do nothing on the database and return tuple (False, CMD_EXECUTION_FAILED).
    - If any exception occurs; rollback, do nothing on the database and return tuple (False, CMD_EXECUTION_FAILED).
"""


def review(conn, user, review_id, business_id, stars):
    cursor = conn.cursor()
    try:
        review_exists_query = "select * from review where review_id = (%s)"
        cursor.execute(review_exists_query, (review_id,))
        review_exists = cursor.fetchone()
        if review_exists is not None:
            return False, NOT_PERMITTED
        elif review_exists is None:
            business_exists_query = "select * from business where business_id = (%s)"
            cursor.execute(business_exists_query, (business_id,))
            business_exists = cursor.fetchone()
            if business_exists is None:
                conn.rollback()
                return False, NOT_PERMITTED
            else:
                user_id = user.user_id
                insert_user_review_business_query = "insert into review(review_id, user_id, business_id, stars) values((%s), (%s), (%s), (%s))"
                cursor.execute(insert_user_review_business_query, (review_id, user_id, business_id, stars,))
                conn.commit()
                return True, CMD_EXECUTION_SUCCESS
    except (Exception, psycopg2.DatabaseError):
        conn.rollback()
        return False, CMD_EXECUTION_FAILED


"""
    Subscribe authenticated user to new membership.
    - Return type is a tuple, 1st element is a user object and 2nd element is the response message from messages.py.
    - If target membership does not exist on the database, return tuple (None, SUBSCRIBE_MEMBERSHIP_NOT_FOUND).
    - If the new membership's max_parallel_sessions < current membership's max_parallel_sessions, return tuple (None, SUBSCRIBE_MAX_PARALLEL_SESSIONS_UNAVAILABLE).
    - If the operation is successful, commit changes and return tuple (user, CMD_EXECUTION_SUCCESS).
    - If any exception occurs; rollback, do nothing on the database and return tuple (None, CMD_EXECUTION_FAILED).
"""


def subscribe(conn, user, membership_id):
    cursor = conn.cursor()
    try:
        membership_exists_query = "select * from membership where membership_id = (%s);"
        cursor.execute(membership_exists_query, (str(membership_id),))
        membership_exists = cursor.fetchone()
        if membership_exists is None:
            return None, SUBSCRIBE_MEMBERSHIP_NOT_FOUND
        else:
            user_id = user.user_id
            max_parallel_sessions_new = int(membership_exists[2])
            old_membership_query = "select * from membership m, subscription s where m.membership_id = s.membership_id and s.user_id = (%s);"
            cursor.execute(old_membership_query, (user_id,))
            old_membership_exists = cursor.fetchone()
            if old_membership_exists is None:
                insert_new_subscription = "insert into subscription(user_id, membership_id, time_spent) values((%s), (%s), 0);"
                cursor.execute(insert_new_subscription, (user_id, str(membership_id)))
                conn.commit()
                return user, CMD_EXECUTION_SUCCESS
            else:
                max_parallel_sessions_old = old_membership_exists[2]
                if max_parallel_sessions_old > max_parallel_sessions_new:
                    return None, SUBSCRIBE_MAX_PARALLEL_SESSIONS_UNAVAILABLE
                else:
                    delete_old_subscription = "delete from subscription where user_id = (%s);"
                    cursor.execute(delete_old_subscription, user_id)
                    insert_new_subscription = "insert into subscription(user_id, membership_id, time_spent) values((%s), (%s), 0);"
                    cursor.execute(insert_new_subscription, (user_id, str(membership_id)))
                    conn.commit()
                    return user, CMD_EXECUTION_SUCCESS
    except (Exception, psycopg2.DatabaseError):
        conn.rollback()
        return None, CMD_EXECUTION_FAILED


"""
    Searches for businesses with given search_text.
    - Return type is a tuple, 1st element is a boolean and 2nd element is the response message from messages.py.
    - Print all businesses whose names contain given search_text IN CASE-INSENSITIVE MANNER.
    - If the operation is successful; print businesses found and return tuple (True, CMD_EXECUTION_SUCCESS).
    - If any exception occurs; return tuple (False, CMD_EXECUTION_FAILED).

    Output should be like:
    Id|Name|State|Is_open|Stars
    1|A4 Coffee Ankara|ANK|1|4
    2|Tetra N Caffeine Coffee Ankara|ANK|1|4
    3|Grano Coffee Ankara|ANK|1|5
"""


def search_for_businesses(conn, user, search_text):
    cursor = conn.cursor()
    try:
        business_query = "select * from business where business_name ilike (%s) ESCAPE '' order by business_id;"
        cursor.execute(business_query, ('%' + search_text + '%',))
        business = cursor.fetchall()
        print("Id|Name|State|Is_open|Stars")
        for i in business:
            print("%s|%s|%s|%r|%f" % (i[0], i[1], i[3], i[4], i[5]))
        return True, CMD_EXECUTION_SUCCESS
    except (Exception, psycopg2.DatabaseError):
        return False, CMD_EXECUTION_FAILED


"""
    Suggests combination of these businesses:

        1- Gather the reviews of that user.  From these reviews, find the top state by the reviewed business count.  
        Then, from all open businesses find the businesses that is located in the found state.  
        You should collect top 5 businesses by stars.

        2- Perform the same thing on the Tip table instead of Review table.

        3- Again check the review table to find the businesses get top stars from that user.  
        Among them get the latest reviewed one.  Now you need to find open top 3 businesses that is located in the same state 
        and has the most stars (if there is an equality order by name and get top 3).


    - Return type is a tuple, 1st element is a boolean and 2nd element is the response message from messages.py.    
    - Output format and return format are same with search_for_businesses.
    - Order these businesses by their business_id, in ascending order at the end.
    - If the operation is successful; print businesses suggested and return tuple (True, CMD_EXECUTION_SUCCESS).
    - If any exception occurs; return tuple (False, CMD_EXECUTION_FAILED).
"""


def suggest_businesses(conn, user):
    cursor = conn.cursor()
    try:
        user_id = user.user_id
        user_exist = "select * from subscription where user_id = (%s);"
        cursor.execute(user_exist, (user_id,))
        user = cursor.fetchone()
        if user is None:
            return False, NOT_ALLOWED
        else:
            suggest_business = "select *\
                                from\
                                    (\
                                        (\
                                            select *\
                                            from business bus\
                                            where bus.is_open = TRUE and bus.state in (\
                                                select b.state\
                                                from review r, business b\
                                                where r.user_id = %s and b.business_id = r.business_id\
                                                group by b.state\
                                                order by count(*) DESC\
                                                LIMIT 1\
                                            )\
                                            order by bus.stars DESC\
                                            LIMIT 5\
                                        )\
                                        UNION\
                                        (\
                                            select *\
                                            from business bus\
                                            where bus.is_open = TRUE and bus.state in (\
                                                select b.state\
                                                from tip t, business b\
                                                where t.user_id = %s and b.business_id = t.business_id\
                                                group by b.state\
                                                order by count(*) DESC\
                                                LIMIT 1\
                                            )\
                                            order by bus.stars DESC\
                                            LIMIT 5\
                                        )\
                                        UNION\
                                        (\
                                            select *\
                                            from business bus\
                                            where bus.is_open = TRUE and bus.state in (\
                                                select b.state\
                                                from review r, business b\
                                                where user_id = %s and b.business_id = r.business_id\
                                                order by r.stars DESC, date DESC\
                                                LIMIT 1\
                                            )\
                                        order by bus.stars DESC, business_name\
                                        LIMIT 3\
                                        )\
                                    ) as businesses\
                                order by business_id;"
            cursor.execute(suggest_business, (user_id, user_id, user_id,))
            businesses = cursor.fetchall()
            print("Id|Name|State|Is_open|Stars")
            for i in businesses:
                print("%s|%s|%s|%r|%f" % (i[0], i[1], i[3], i[4], i[5]))
            return True, CMD_EXECUTION_SUCCESS
    except (Exception, psycopg2.DatabaseError):
        return False, CMD_EXECUTION_FAILED


"""
    Create coupons for given user. Coupons should be created by following these steps:

        1- Calculate the score by using the following formula:
            Score = timespent + 10 * reviewcount

        2- Calculate discount percentage using the following formula (threshold given in messages.py):
            actual_discount_perc = score/threshold * 100

        3- If found percentage in step 2 is lower than 25% print the following:
            You don’t have enough score for coupons.

        4- Else if found percentage in step 2 is between 25-50% print the following:
            Creating X% discount coupon.

        5- Else create 50% coupon and remove extra time from user's time_spent:
            Creating 50% discount coupon.

    - Return type is a tuple, 1st element is a boolean and 2nd element is the response message from messages.py.    
    - If the operation is successful (step 4 or 5); return tuple (True, CMD_EXECUTION_SUCCESS).
    - If the operation is not successful (step 3); return tuple (False, CMD_EXECUTION_FAILED).
    - If any exception occurs; return tuple (False, CMD_EXECUTION_FAILED).


"""


def get_coupon(conn, user):
    cursor = conn.cursor()
    try:
        user_id = user.user_id
        premium_exists_query = "select * from subscription where user_id = (%s);"
        cursor.execute(premium_exists_query, (user_id,))
        premium_exists = cursor.fetchone()
        if premium_exists is not None:
            review_count_query = "  select * from ( \
                                        select r.user_id, count(*) \
                                        from review r \
                                        where r.user_id = (%s)\
                                        group by r.user_id\
                                    ) as review_count;"
            cursor.execute(review_count_query, (user_id,))
            review_count = cursor.fetchone()[1]
            spent_time_query = " select * \
                                from subscription \
                                where user_id = (%s)"
            cursor.execute(spent_time_query, (user_id,))
            spent_time = cursor.fetchone()[2]
            Score = (spent_time + review_count * 10)
            actual_discount_percentage = (Score / threshold) * 100
            if actual_discount_percentage < 25:
                return False, NOT_ENOUGH_SCORE
            elif 25 <= actual_discount_percentage < 50:
                print("Creating X% discount coupon.")
                return True, CMD_EXECUTION_SUCCESS
            elif actual_discount_percentage >= 50:
                print("Creating 50% discount coupon.")
                return True, CMD_EXECUTION_SUCCESS
        else:
            return False, NOT_ALLOWED
    except (Exception, psycopg2.DatabaseError):
        return False, CMD_EXECUTION_FAILED
    return False, CMD_EXECUTION_FAILED

/* Question 1 */
select distinct users.user_id, users.user_name, users.review_count-users.fans as diff
from users, review, business
where users.review_count > users.fans and
      review.user_id=users.user_id and
      review.business_id=business.business_id and
      business.stars > 3.5
order by diff DESC, users.user_id DESC;

/* Question 2 */
select distinct users.user_name, business.business_name, tip.date, tip.compliment_count
from users, business, tip
where tip.user_id = users.user_id and
        business.business_id = tip.business_id and
        business.state = 'TX' and
        business.is_open = true and
        tip.compliment_count > 2
order by tip.compliment_count DESC, tip.date DESC;

/* Question 3 */
select user1.user_name, friendly.counto
from (select friendship.user_id1, count(*) as counto
      from friend friendship
      group by friendship.user_id1
      order by count(*) desc) as friendly, users user1
where user1.user_id=friendly.user_id1
order by friendly.counto desc, user1.user_name desc
limit 20;

/* Question 4 */
select user1.user_name, user1.average_stars, user1.yelping_since
from users user1
where user1.user_id in (select  distinct user2.user_id
                        from    users user2, business, review
                        where   user2.user_id=review.user_id and
                                business.business_id=review.business_id and
                                review.stars < business.stars)
order by user1.average_stars DESC, yelping_since DESC;

/* Question 5 */
select business.business_name, business.state, business.stars
from business, (select tip.business_id, count(*) as sayi
                from tip
                where tip.tip_text like '%good%' and tip.date > '2019-12-31'::date and tip.date < '2021-01-01'::date
                group by business_id
                order by count(*) DESC) as counts
where business.business_id=counts.business_id and business.is_open=true and counts.sayi >= ALL(select count(*) as sayi
                                                                                               from tip
                                                                                               where tip.tip_text like '%good%' and tip.date > '2019-12-31'::date and tip.date < '2021-01-01'::date
                                                                                               group by business_id
                                                                                               order by count(*) DESC)
order by business.stars DESC, business.business_name DESC;

/* Question 6 */
select distinct user1.user_name, user1.yelping_since, user1.average_stars
from users user1, friend friends
where user1.user_id = friends.user_id1 and user1.average_stars < ALL ( select user2.average_stars
                                                                       from users user2, friend
                                                                       where friend.user_id1=user1.user_id and friend.user_id2=user2.user_id )
order by user1.average_stars DESC, user1.yelping_since DESC;

/* Question 7 */
select business.state, avg(business.stars)
from business
group by business.state
order by avg(business.stars) DESC
LIMIT 10;

/* Question 8 */
select good.yyyy, total.average
from (select extract(year from tipo.date) as yyyy, count(*) as tcount, avg(tipo.compliment_count) as average
      from tip tipo
      group by yyyy
     ) as total,
     (select extract(year from tipik.date) as yyyy, count(*) as goods
      from tip tipik
      where tipik.compliment_count >= 1
      group by yyyy
     ) as good
where total.yyyy = good.yyyy and total.tcount/100 < good.goods
order by good.yyyy;

/* Question 9 */
select users.user_name
from
    (select distinct review.user_id
     from users, review
     where review.user_id=users.user_id
         EXCEPT
     select distinct review.user_id
     from review, business
     where review.business_id=business.business_id and business.stars <= 3.5 ) as h, users
where users.user_id = h.user_id
order by users.user_name;

/* Question 10 */
select allreviews.business_name, allreviews.yyyy, allreviews.ave
from (select business.business_name, business.business_id, extract(year from revo.date) as yyyy, cast(sum(revo.stars) as float)/cast(count(*) as float) as ave
      from review revo, business
      where business.business_id = revo.business_id
      group by yyyy, business.business_name, business.business_id) as allreviews, business bus
where bus.business_id = allreviews.business_id and allreviews.ave > 3 and bus.business_id in
                                                                          (select rev.business_id
                                                                           from review rev, business bus
                                                                           where rev.business_id = bus.business_id
                                                                           group by rev.business_id
                                                                           having count(*) > 1000)
order by yyyy, allreviews.business_name;

/* Question 11 */
select usert.user_name, result.cool, result.useful, result.difference
from users usert, (select use.user_id, sum(rev.cool) as cool, sum(rev.useful) as useful, (sum(rev.useful) - sum(rev.cool)) as difference
                   from users use, review rev
                   where use.user_id = rev.user_id
                   group by use.user_id
                   having sum(rev.useful) > sum(rev.cool)) as result
where result.user_id = usert.user_id
order by result.difference DESC, usert.user_name DESC;

/* Question 12 */
SELECT DISTINCT
    friendship.user_id1, friendship.user_id2, rev1.business_id, rev1.stars
FROM
    (
        SELECT
            CASE WHEN friend.user_id1 < friend.user_id2 THEN friend.user_id1 ELSE friend.user_id2 END as user_id1,
            CASE WHEN friend.user_id1 < friend.user_id2 THEN friend.user_id2 ELSE friend.user_id1 END as user_id2
        FROM friend
    ) as friendship, review rev1, review rev2
where friendship.user_id1 = rev1.user_id and friendship.user_id2=rev2.user_id and rev1.business_id = rev2.business_id and rev1.stars=rev2.stars
order by rev1.business_id DESC, rev1.stars DESC;

/* Question 13 */
select bus.stars, bus.state, count(*)
from business bus
where bus.is_open = true
group by CUBE((bus.stars), (bus.state));

/* Question 14 */
select total_count.user_id, total_count.review_count, total_count.fans, RANK
from (select user2.*, rank() over(partition by user2.fans order by user2.review_count DESC) as RANK
      from users user2
      where user2.fans >= 50 and user2.fans <= 60) as total_count
where RANK <= 3;
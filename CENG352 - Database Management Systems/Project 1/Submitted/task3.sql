/* Trjgger 1 */
create function userUpdater() returns trigger as $UserReview$
BEGIN
    update users
    set review_count = review_count + 1
    where NEW.user_id = user_id;
    return new;
end;
$UserReview$ language plpgsql;

create trigger UserReview
    after insert on review
    for each row
execute procedure userUpdater();

/* Trigger 2 */
create function ReviewAndTipDeleter() returns trigger as $ZeroReview$
BEGIN
    if(new.stars = 0) then
        delete from review
        where new.user_id=review.user_id;
    end if;
    if(new.stars = 0) then
        delete from tip
        where tip.user_id = new.user_id;
    end if;
    return new;
end;
$ZeroReview$ language plpgsql;


create trigger ZeroReview
    after insert on review
    for each row
execute procedure ReviewAndTipDeleter();

/* View */
create view BusinessCount as
select bus.business_id, bus.business_name, count(*)
from business bus, review rev
where bus.business_id = rev.business_id
group by bus.business_id, bus.business_name

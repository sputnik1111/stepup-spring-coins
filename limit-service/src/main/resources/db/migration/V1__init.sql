create table user_limit(
	user_id bigint primary key,
	current_limit bigint not null
);

create table user_pay_confirm(
    id bigint primary key generated always as identity,
    pay_id UUID unique not null,
    user_id bigint not null,
    sub_limit bigint not null
);

create table job_run(
    job_name varchar(255) primary key,
    next_run timestamptz not null
);

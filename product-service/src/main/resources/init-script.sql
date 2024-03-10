create table users(
	id bigserial primary key,
	username varchar(255) unique
);

create table user_product(
	id bigserial primary key,
	user_id bigint not null,
	account varchar(255) not null,
	balance bigint not null,
	type_product varchar(255) not null,
	CONSTRAINT fk_users
      FOREIGN KEY(user_id)
	  REFERENCES users(id) ON DELETE CASCADE
);
use covidtracker;
create table countryData (
	country varchar(20),
    infected bigint,
    recovered bigint,
    deceased bigint,
    tested bigint,
    updateTime varchar(30),
    moredata varchar(500),
    historyData varchar(500),
    sourceURL varchar(500),
    primary key (country)
	);
create table countryUpdate(
	country varchar(20),
    updateTime varchar(30),
    infected bigint,
    recovered bigint,
    deceased bigint,
    tested bigint,
    primary key(country, updateTime),
    foreign key(country) references countryData(country)
    );	
create table countryPopulation(
	country varchar(20),
    population bigint,
    primary key (country)
    );
select * from countryData where country = "VIETNAM";
delete from countryPopulation
where country not in (select country from countryData);

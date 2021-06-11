show databases;
create database covidProject;
use covidProject;
drop table countryData;
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
    
select * from countryData;

drop table countryUpdate;
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

select count(c1.country), sum(c1.population)
from countryPopulation c1, countryData c2
where c1.country = c2.country;

SELECT SUM(c1.population) as populations, COUNT(c1.country) as numCountries FROM countryPopulation c1, countryData c2 WHERE c1.country = c2.country;
SELECT SUM(deceased) as deceased, COUNT(deceased) as countries FROM countryData WHERE deceased != -1;
SELECT c1.country FROM countryData c1 WHERE c1.country not in (select c2.country from countryPopulation c2);
select source from countryData where country = "JAPAN";
select * from countryUpdate;
drop table countryPopulation;
create table countryPopulation(
	country varchar(20),
    population bigint,
    primary key (country)
    );
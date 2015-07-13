CREATE TABLE IF NOT EXISTS `EmailQueue` (
`id` int(10) NOT NULL AUTO_INCREMENT,
`from_mail` varchar(30) NOT NULL,
`to_mail` varchar(30) NOT NULL,
`subject` varchar(100) NOT NULL,
`body` text NOT NULL,
`state` ENUM('N', 'W', 'D') NOT NULL DEFAULT 'N',
`sent_on` timestamp NULL DEFAULT NULL,
primary key (id)
);
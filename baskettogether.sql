CREATE TABLE `User` (
  `_id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `salt` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `nickname` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `country` char(2) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `regType` tinyint(2) NOT NULL,
  `snsId` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `photoUrl` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `regDts` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastLoginAt` datetime DEFAULT NULL,
  `deviceId` text COLLATE utf8mb4_unicode_ci,
  `deviceType` char(1) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`_id`),
  UNIQUE KEY `_id_UNIQUE` (`_id`),
  UNIQUE KEY `user_id_UNIQUE` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

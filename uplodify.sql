-- --------------------------------------------------------
-- 主机:                           127.0.0.1
-- 服务器版本:                        5.7.21-log - MySQL Community Server (GPL)
-- 服务器操作系统:                      Win64
-- HeidiSQL 版本:                  9.5.0.5249
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- 导出 uplodify 的数据库结构
CREATE DATABASE IF NOT EXISTS `uplodify` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `uplodify`;

-- 导出  表 uplodify.file_operation_log 结构
CREATE TABLE IF NOT EXISTS `file_operation_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '键',
  `file_name` varchar(255) DEFAULT NULL COMMENT '文件名',
  `md5` varchar(255) DEFAULT NULL COMMENT '文件md5值',
  `flag` int(1) unsigned zerofill DEFAULT NULL COMMENT '是否删除标志位',
  `upload_time` datetime DEFAULT NULL COMMENT '上传时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='文件操作日志';

-- 数据导出被取消选择。
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

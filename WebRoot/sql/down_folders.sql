USE [HttpUploader6]
GO
/****** 对象:  Table [dbo].[down_folders]    脚本日期: 05/19/2015 13:59:27 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[down_folders](
	[fd_id]			[int] IDENTITY(1,1) NOT NULL,
	[fd_name]		[nvarchar](50) COLLATE Chinese_PRC_CI_AS NULL,
	[fd_uid]		[int] NULL,
	[fd_mac]		[nvarchar](50) COLLATE Chinese_PRC_CI_AS NULL,
	[fd_pathLoc]	[nvarchar](255) COLLATE Chinese_PRC_CI_AS NULL,
	[fd_complete]	[bit] NULL,
	[fd_id_old]		[int] NULL CONSTRAINT [DF_down_folders_fd_id_old]  DEFAULT ((0)),
	[fd_percent]	[nvarchar](7) COLLATE Chinese_PRC_CI_AS NULL
) ON [PRIMARY]

GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件夹名称。' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'down_folders', @level2type=N'COLUMN',@level2name=N'fd_name'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'用户ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'down_folders', @level2type=N'COLUMN',@level2name=N'fd_uid'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'本地文件路径' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'down_folders', @level2type=N'COLUMN',@level2name=N'fd_pathLoc'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'与xdb_folders.fd_id对应。' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'down_folders', @level2type=N'COLUMN',@level2name=N'fd_id_old'
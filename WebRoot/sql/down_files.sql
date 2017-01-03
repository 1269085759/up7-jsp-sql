USE [HttpUploader6]
GO
/****** 对象:  Table [dbo].[down_files]    脚本日期: 05/19/2015 13:58:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[down_files](
	[f_id]			[int] IDENTITY(1,1) NOT NULL,
	[f_uid]			[int] NOT NULL CONSTRAINT [DF_down_files_f_uid]  DEFAULT ((0)),
	[f_mac]			[nvarchar](50) COLLATE Chinese_PRC_CI_AS NULL,
	[f_pathLoc]		[nvarchar](255) COLLATE Chinese_PRC_CI_AS NULL,
	[f_pathSvr]		[nvarchar](255) COLLATE Chinese_PRC_CI_AS NULL,
	[f_lengthLoc]	[nvarchar](19) COLLATE Chinese_PRC_CI_AS NULL,
	[f_lengthSvr]	[nvarchar](19) COLLATE Chinese_PRC_CI_AS NULL,
	[f_complete]	[bit] NULL CONSTRAINT [DF_down_files_f_complete]  DEFAULT ((0)),
	[f_percent]		[nvarchar](6) COLLATE Chinese_PRC_CI_AS NULL CONSTRAINT [DF_down_files_f_percent]  DEFAULT (N'0%'),
	[f_fdID]		[int] NULL CONSTRAINT [DF_down_files_f_fdID]  DEFAULT ((0)),
	[f_pidRoot]		[int] NULL CONSTRAINT [DF_down_files_f_pidRoot]  DEFAULT ((0)),
	[f_pid]			[int] NULL CONSTRAINT [DF_down_files_f_pid]  DEFAULT ((0))
) ON [PRIMARY]

GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'down_files', @level2type=N'COLUMN',@level2name=N'f_id'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'用户ID,用来与第三方系统整合。' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'down_files', @level2type=N'COLUMN',@level2name=N'f_uid'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'MAC地址，用来识别不同电脑的下载任务' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'down_files', @level2type=N'COLUMN',@level2name=N'f_mac'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'本地文件路径。' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'down_files', @level2type=N'COLUMN',@level2name=N'f_pathLoc'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'服务器文件地址。' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'down_files', @level2type=N'COLUMN',@level2name=N'f_pathSvr'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'本地文件长度（已下载文件长度）' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'down_files', @level2type=N'COLUMN',@level2name=N'f_lengthLoc'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'服务器文件长度' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'down_files', @level2type=N'COLUMN',@level2name=N'f_lengthSvr'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'是否已经下载完成' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'down_files', @level2type=N'COLUMN',@level2name=N'f_complete'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'已下载进度。' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'down_files', @level2type=N'COLUMN',@level2name=N'f_percent'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'与down_folders.fd_id对应' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'down_files', @level2type=N'COLUMN',@level2name=N'f_fdID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'根级文件夹ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'down_files', @level2type=N'COLUMN',@level2name=N'f_pidRoot'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'父级文件夹ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'down_files', @level2type=N'COLUMN',@level2name=N'f_pid'
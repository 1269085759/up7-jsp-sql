USE [HttpUploader7]
GO
/****** 对象:  Table [dbo].[xdb_files]    脚本日期: 06/10/2014 15:02:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[xdb_files](
	[fid]				[int] IDENTITY(1,1) NOT NULL,
	[f_pid]				[int] NULL CONSTRAINT [DF_xdb_files_f_pid]  DEFAULT ((0)),
	[f_pidRoot]			[int] NULL CONSTRAINT [DF_xdb_files_f_pidRoot]  DEFAULT ((0)),
	[f_fdTask]			[bit] NULL CONSTRAINT [DF_xdb_files_f_fdTask]  DEFAULT ((0)),
	[f_fdID]			[int] NULL CONSTRAINT [DF_xdb_files_f_fdID]  DEFAULT ((0)),
	[f_fdChild]			[bit] NULL CONSTRAINT [DF_xdb_files_f_fdChild]  DEFAULT ((0)),
	[uid]				[int] NULL CONSTRAINT [DF_xdb_files_uid]  DEFAULT ((0)),
	[FileNameLocal]		[varchar](255) COLLATE Chinese_PRC_CI_AS NULL,
	[FileNameRemote]	[varchar](255) COLLATE Chinese_PRC_CI_AS NULL,
	[FilePathLocal]		[varchar](512) COLLATE Chinese_PRC_CI_AS NULL,
	[FilePathRemote]	[varchar](512) COLLATE Chinese_PRC_CI_AS NULL,
	[FilePathRelative]	[varchar](512) COLLATE Chinese_PRC_CI_AS NULL,
	[FileMD5]			[varchar](40) COLLATE Chinese_PRC_CI_AS NULL,
	[FileLength]		[varchar](19) COLLATE Chinese_PRC_CI_AS NULL CONSTRAINT [DF_xdb_files_FileLength]  DEFAULT ((0)),
	[FileSize]			[varchar](10) COLLATE Chinese_PRC_CI_AS NULL CONSTRAINT [DF_xdb_files_FileSize]  DEFAULT ('0Bytes'),
	[FilePos]			[varchar](19) COLLATE Chinese_PRC_CI_AS NULL CONSTRAINT [DF_xdb_files_FilePos]  DEFAULT ((0)),
	[PostedLength]		[varchar](19) COLLATE Chinese_PRC_CI_AS NULL CONSTRAINT [DF_xdb_files_PostedLength]  DEFAULT ((0)),
	[PostedPercent]		[varchar](6) COLLATE Chinese_PRC_CI_AS NULL CONSTRAINT [DF_xdb_files_PostedPercent]  DEFAULT ('0%'),
	[PostComplete]		[bit] NULL CONSTRAINT [DF_xdb_files_PostComplete]  DEFAULT ((0)),
	[PostedTime]		[datetime] NULL CONSTRAINT [DF_xdb_files_PostedTime]  DEFAULT (getdate()),
	[IsDeleted]			[bit] NULL CONSTRAINT [DF_xdb_files_IsDeleted]  DEFAULT ((0)),
 CONSTRAINT [PK_xdb_files] PRIMARY KEY CLUSTERED 
(
	[fid] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'父级文件夹ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'xdb_files', @level2type=N'COLUMN',@level2name=N'f_pid'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'根级文件夹ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'xdb_files', @level2type=N'COLUMN',@level2name=N'f_pidRoot'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'表示是否是一个文件夹上传任务' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'xdb_files', @level2type=N'COLUMN',@level2name=N'f_fdTask'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件夹详细ID，与xdb_folders.fd_id对应' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'xdb_files', @level2type=N'COLUMN',@level2name=N'f_fdID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'是否是文件夹中的子项' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'xdb_files', @level2type=N'COLUMN',@level2name=N'f_fdChild'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件在本地电脑中的名称。例：QQ.exe ' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'xdb_files', @level2type=N'COLUMN',@level2name=N'FileNameLocal'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件在服务器中的名称。一般为文件MD5+扩展名。' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'xdb_files', @level2type=N'COLUMN',@level2name=N'FileNameRemote'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件在本地电脑中的完整路径。
示例：D:\Soft\QQ.exe
' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'xdb_files', @level2type=N'COLUMN',@level2name=N'FilePathLocal'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件在服务器中的完整路径。
示例：F:\ftp\user1\QQ2012.exe
' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'xdb_files', @level2type=N'COLUMN',@level2name=N'FilePathRemote'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件在服务器中的相对路径。
示例：/www/web/upload/QQ2012.exe
' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'xdb_files', @level2type=N'COLUMN',@level2name=N'FilePathRelative'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件MD5' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'xdb_files', @level2type=N'COLUMN',@level2name=N'FileMD5'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件总长度。以字节为单位
最大值：9,223,372,036,854,775,807
' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'xdb_files', @level2type=N'COLUMN',@level2name=N'FileLength'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'格式化的文件尺寸。示例：10MB' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'xdb_files', @level2type=N'COLUMN',@level2name=N'FileSize'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件续传位置。
最大值：9,223,372,036,854,775,807
' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'xdb_files', @level2type=N'COLUMN',@level2name=N'FilePos'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'已上传长度。以字节为单位。
最大值：9,223,372,036,854,775,807
' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'xdb_files', @level2type=N'COLUMN',@level2name=N'PostedLength'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'已上传百分比。示例：10%' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'xdb_files', @level2type=N'COLUMN',@level2name=N'PostedPercent'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'是否已上传完毕。' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'xdb_files', @level2type=N'COLUMN',@level2name=N'PostComplete'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件上传时间' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'xdb_files', @level2type=N'COLUMN',@level2name=N'PostedTime'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'是否已删除。' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'xdb_files', @level2type=N'COLUMN',@level2name=N'IsDeleted'
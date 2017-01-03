USE [HttpUploader6]
GO
/****** 对象:  StoredProcedure [dbo].[spUpdateProcess]    脚本日期: 07/31/2014 16:54:12 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		Xproer
-- Create date: 2012-10-25
-- Description:	更新文件进度
-- =============================================
CREATE PROCEDURE [dbo].[spUpdateProcess]
	-- Add the parameters for the stored procedure here
	@FilePos int
	,@uid int
	,@fid int
	,@PostedLength nvarchar(19)
	,@PostedPercent nvarchar(6)

AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    DECLARE @sql nvarchar(4000);
	DECLARE @ParamDef nvarchar(500);

    SET @sql=N'update xdb_files set FilePos=@FilePos,PostedLength=@PostedLength,PostedPercent=@PostedPercent where uid=@uid and fid=@fid'
	SET @ParamDef = N'@FilePos int
					,@uid int
					,@fid int
					,@PostedLength nvarchar(19)
					,@PostedPercent nvarchar(6)';

    -- Insert statements for procedure here
	EXEC sp_executesql @sql,@ParamDef,@FilePos,@uid,@fid,@PostedLength,@PostedPercent
END


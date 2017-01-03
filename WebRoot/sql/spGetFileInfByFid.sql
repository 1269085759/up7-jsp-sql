USE [HttpUploader6]
GO
/****** 对象:  StoredProcedure [dbo].[spGetFileInfByFid]    脚本日期: 07/31/2014 16:53:29 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		Xproer
-- Create date: 2012-10-25
-- Description:	根据ID获取文件详细信息
-- =============================================
CREATE PROCEDURE [dbo].[spGetFileInfByFid]
	-- Add the parameters for the stored procedure here
	@fid int	--文件ID。
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	DECLARE @sql nvarchar(1000);
	DECLARE @ParamDef nvarchar(500);

	SET @sql = N'select top 1 * from xdb_files where fid=@fid';
	SET @ParamDef = N'@fid int';
    -- Insert statements for procedure here
	EXECUTE sp_executesql @sql, @ParamDef,@fid

END


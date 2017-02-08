USE [up7]
GO
/****** 对象:  StoredProcedure [dbo].[fd_fileProcess]    脚本日期: 04/03/2016 17:48:05 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO



-- =============================================
-- Author:		quanwuling
-- Create date: 2016-04-01
-- Description:	更新文件夹子文件进度
-- =============================================
CREATE PROCEDURE [dbo].[fd_fileProcess]
	-- Add the parameters for the stored procedure here
	 @f_pos bigint
	,@uid int
	,@idSvr int	--对于文件和文件夹这个idSvr都与up7_files.f_id对应
	,@lenSvr bigint
	,@perSvr nvarchar(6)
	,@complete bit
	,@fd_idSvr int				--文件夹ID	
	,@fd_lenSvr bigint
	,@fd_perSvr nvarchar(6)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    DECLARE @sql nvarchar(4000);
	DECLARE @ParamDef nvarchar(500);
    DECLARE @fd_sql nvarchar(4000);
	DECLARE @fd_params nvarchar(500);	

	--更新文件进度
    SET @sql=N'update up7_files set f_pos=@f_pos,f_lenSvr=@lenSvr,f_perSvr=@perSvr,f_complete=@complete where f_uid=@uid and f_id=@idSvr'
	SET @ParamDef = N'@f_pos bigint
					,@uid int
					,@idSvr int
					,@lenSvr bigint
					,@perSvr nvarchar(6)
					,@complete bit';
	
	--更新文件夹进度
	SET @fd_sql = N'update up7_files set f_lenSvr=@fd_lenSvr,f_perSvr=@fd_perSvr where f_uid=@uid and f_id=@fd_idSvr'
	SET @fd_params = N'@uid int
					,@fd_idSvr int
					,@fd_lenSvr bigint
					,@fd_perSvr nvarchar(6)';

    -- Insert statements for procedure here
	EXEC sp_executesql @sql,@ParamDef,@f_pos,@uid,@idSvr,@lenSvr,@perSvr,@complete
	EXEC sp_executesql @fd_sql,@fd_params,@uid,@fd_idSvr,@fd_lenSvr,@fd_perSvr
END

export default function KhongGianVanBan({ children, banXem }) {
  return (
    <div className="khung-khong-gian-van-ban">
      <div className={banXem ? 'khong-gian-van-ban' : undefined}>
        <div className="min-w-0 space-y-4">{children}</div>
        {banXem}
      </div>
    </div>
  )
}

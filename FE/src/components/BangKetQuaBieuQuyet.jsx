const KET_QUA_NGHI_QUYET = {
  THONG_QUA: 'Nghị quyết được thông qua',
  KHONG_THONG_QUA: 'Nghị quyết không được thông qua',
  CHUA_DAT_DA_SO: 'Nghị quyết chưa đạt đa số',
}

const MAU_LUA_CHON = {
  'Đồng ý': 'bg-green-500',
  'Không đồng ý': 'bg-rose-500',
  'Không ý kiến': 'bg-slate-400',
  'Phiếu trắng': 'bg-slate-400',
}

const DINH_DANG_PHAN_TRAM = new Intl.NumberFormat('vi-VN', {
  style: 'percent', maximumFractionDigits: 1,
})

export default function BangKetQuaBieuQuyet({ ketQua, hienLyDo = false }) {
  const { tongSoPhieu, ketQuaNghiQuyet, hoaPhieu, luaChonThangId, luaChon = [], lyDoKhongDongY } = ketQua
  const luaChonThang = luaChon.find((muc) => muc.luaChonId === luaChonThangId)
  return (
    <div className="space-y-3">
      <p className="font-medium">Tổng số phiếu · {tongSoPhieu}</p>
      {tongSoPhieu === 0 && <p>Chưa có phiếu để xác định kết quả.</p>}
      {ketQuaNghiQuyet && <p className="font-semibold">{KET_QUA_NGHI_QUYET[ketQuaNghiQuyet]}</p>}
      {!ketQuaNghiQuyet && hoaPhieu && <p>Hòa phiếu. Chưa xác định phương án thắng.</p>}
      {luaChonThang && <p>Nhiều phiếu nhất · {luaChonThang.noiDung}</p>}

      <ul className="space-y-3">
        {luaChon.map((muc) => {
          const tyLe = tongSoPhieu > 0 ? muc.soPhieu / tongSoPhieu : 0
          return (
            <li key={muc.luaChonId}>
              <div className="mb-1.5 flex flex-wrap justify-between gap-x-3 font-medium">
                <span className="wrap-anywhere">{muc.noiDung}</span>
                <span className="tabular-nums text-slate-600 phong-toi:text-phong-chu-phu">{muc.soPhieu} phiếu ({DINH_DANG_PHAN_TRAM.format(tyLe)})</span>
              </div>
              <div aria-hidden="true" className="h-2 overflow-hidden rounded-full bg-slate-200 phong-toi:bg-[#35435a]">
                <div className={`h-full rounded-full ${MAU_LUA_CHON[muc.noiDung] ?? 'bg-blue-500'}`}
                  style={{ width: `${tyLe * 100}%` }} />
              </div>
            </li>
          )
        })}
      </ul>

      {hienLyDo && lyDoKhongDongY?.length > 0 && (
        <div className="border-t border-slate-200 phong-toi:border-phong-vien pt-3">
          <p className="font-semibold">Lý do không đồng ý</p>
          <ul className="mt-1 list-disc space-y-1 pl-5">
            {lyDoKhongDongY.map((lyDo, index) => <li className="wrap-anywhere" key={index}>{lyDo}</li>)}
          </ul>
        </div>
      )}
    </div>
  )
}

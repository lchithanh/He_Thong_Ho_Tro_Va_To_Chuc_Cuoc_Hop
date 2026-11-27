import { useEffect, useState } from 'react'
import Bell from 'lucide-react/dist/esm/icons/bell.mjs'
import { docPhanHoiJson, goiApi } from '../api/apiClient.js'
import { useDongBoDuLieu } from '../services/useDongBoDuLieu.js'
import { dinhDangNgayGio } from '../utils/thoiGianCuocHop.js'
import { HopThoaiHoSo } from './GiaoDienPhongHop.jsx'
import { LOP_CHU_PHU, LOP_NUT_PHU } from './giaoDienPhongHop.js'

const URL_HOP_THU = '/api/thong-bao'

export default function HopThongBao({ token }) {
  const phien = useDongBoDuLieu()
  const [mo, setMo] = useState(false)
  const [trang, setTrang] = useState(0)
  const [hopThu, setHopThu] = useState(null)
  const [loi, setLoi] = useState('')
  const [dangLuu, setDangLuu] = useState(false)

  useEffect(() => {
    const controller = new AbortController()
    goiApi(URL_HOP_THU + '?trang=' + trang, { token, signal: controller.signal })
      .then((response) => docPhanHoiJson(response, 'Không tải được thông báo.'))
      .then((duLieu) => {
        if (controller.signal.aborted) return
        setHopThu(duLieu)
        setLoi('')
      })
      .catch((error) => { if (!controller.signal.aborted) setLoi(error.message) })
    return () => controller.abort()
  }, [token, trang, phien])

  async function doc(id) {
    setDangLuu(true)
    setLoi('')
    try {
      const url = id == null ? URL_HOP_THU : URL_HOP_THU + '/' + id
      await docPhanHoiJson(await goiApi(url + '/da-doc', { token, method: 'PUT' }), 'Không cập nhật được thông báo.')
    } catch (error) {
      setLoi(error.message)
    } finally {
      setDangLuu(false)
    }
  }

  const chuaDoc = hopThu?.chuaDoc ?? 0
  return <>
    <button type="button" className={LOP_NUT_PHU + ' relative'} onClick={() => setMo(true)}
      aria-label={chuaDoc ? `Thông báo, ${chuaDoc} chưa đọc` : 'Thông báo'} title="Thông báo">
      <Bell size={17} aria-hidden="true" />
      {chuaDoc > 0 && <span aria-hidden="true" className="absolute -right-1.5 -top-1.5 rounded-full bg-rose-700 px-1.5 text-xs text-white">
        {chuaDoc > 99 ? '99+' : chuaDoc}
      </span>}
    </button>
    {mo && <HopThoaiHoSo tieuDe="Thông báo" Icon={Bell} dangLuu={dangLuu} onDong={() => setMo(false)}>
      <div className="space-y-3 text-sm">
        <div className="flex justify-end">
          <button type="button" className={LOP_NUT_PHU} disabled={dangLuu} onClick={() => setMo(false)}>Đóng</button>
        </div>
        {loi && <p role="alert" className="text-rose-700 phong-toi:text-phong-do">{loi}</p>}
        {!hopThu && !loi && <p role="status">Đang tải thông báo…</p>}
        {hopThu && <>
          <div className="flex flex-wrap items-center justify-between gap-2">
            <span>{chuaDoc} chưa đọc</span>
            <button type="button" className={LOP_NUT_PHU} disabled={dangLuu || !chuaDoc} onClick={() => doc()}>
              Đánh dấu tất cả đã đọc
            </button>
          </div>
          {!hopThu.danhSach.length && <p className={LOP_CHU_PHU}>Chưa có thông báo.</p>}
          <ul className="space-y-2">
            {hopThu.danhSach.map((muc) => <li key={muc.id} className="rounded-lg border p-3">
              <div className="flex flex-wrap items-start justify-between gap-2">
                <strong>{muc.tieuDe}</strong>
                {!muc.daDoc && <button type="button" className={LOP_NUT_PHU} disabled={dangLuu} onClick={() => doc(muc.id)}>
                  Đánh dấu đã đọc<span className="sr-only"> {muc.tieuDe}</span>
                </button>}
              </div>
              <p className="mt-1 whitespace-pre-wrap wrap-anywhere">{muc.noiDung}</p>
              <p className={LOP_CHU_PHU}>{dinhDangNgayGio(muc.ngayTao)}</p>
            </li>)}
          </ul>
          {(trang > 0 || hopThu.conTrang) && <div className="flex items-center justify-between gap-2">
            <button type="button" className={LOP_NUT_PHU} disabled={trang === 0} onClick={() => setTrang(trang - 1)}>Trang trước</button>
            <span>Trang {trang + 1}</span>
            <button type="button" className={LOP_NUT_PHU} disabled={!hopThu.conTrang} onClick={() => setTrang(trang + 1)}>Trang sau</button>
          </div>}
        </>}
      </div>
    </HopThoaiHoSo>}
  </>
}

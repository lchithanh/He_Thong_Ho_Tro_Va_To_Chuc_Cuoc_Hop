import Inbox from 'lucide-react/dist/esm/icons/inbox.mjs'
import { TieuDeHoSo } from '../../../components/GiaoDienPhongHop.jsx'
import { LOP_NUT_CHINH } from '../../components/giaoDienPhongHop.js'

export default function TiepNhanDeXuatAction({ onClick, disabled }) {
  return (
    <section className="rounded-xl border border-blue-100 bg-blue-50 p-4">
      <TieuDeHoSo Icon={Inbox} cap="h2" phu="Sau khi tiếp nhận, hồ sơ sẽ mở mục Lịch và kế hoạch."
        thaoTac={<button type="button" className={LOP_NUT_CHINH} onClick={onClick} disabled={disabled}>
          {disabled ? 'Đang tiếp nhận…' : 'Tiếp nhận đề xuất'}
        </button>}>
        Hồ sơ chờ tiếp nhận
      </TieuDeHoSo>
    </section>
  )
}

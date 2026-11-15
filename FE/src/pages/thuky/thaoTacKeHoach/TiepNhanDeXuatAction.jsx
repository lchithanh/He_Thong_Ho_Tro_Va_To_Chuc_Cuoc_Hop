import NutThaoTac from './NutThaoTac.jsx'

export default function TiepNhanDeXuatAction({ onClick, disabled }) {
  return (
    <section className="rounded-xl border border-amber-200 bg-amber-50 p-4">
      <p className="text-sm text-amber-900">
        Tiếp nhận hồ sơ để bắt đầu chuẩn bị lịch và kế hoạch cuộc họp.
      </p>
      <div className="mt-3">
        <NutThaoTac loai="chinh" onClick={onClick} disabled={disabled}>
          {disabled ? 'Đang tiếp nhận…' : 'Tiếp nhận đề xuất'}
        </NutThaoTac>
      </div>
    </section>
  )
}

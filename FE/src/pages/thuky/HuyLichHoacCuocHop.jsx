import { useId, useState } from 'react'

export default function HuyLichHoacCuocHop({ ten, canhBao, onHuy }) {
  const idLyDo = useId()
  const [mo, setMo] = useState(false)
  const [lyDo, setLyDo] = useState('')
  const [loi, setLoi] = useState('')
  const [dangHuy, setDangHuy] = useState(false)

  async function xacNhan(event) {
    event.preventDefault()
    if (dangHuy || !lyDo.trim()) return
    setDangHuy(true)
    setLoi('')
    try {
      await onHuy(lyDo.trim())
      setMo(false)
      setLyDo('')
    } catch (error) {
      setLoi(error.message)
    } finally {
      setDangHuy(false)
    }
  }

  if (!mo) {
    return <button type="button" onClick={() => setMo(true)}
      className="rounded-lg border border-red-200 px-3 py-2 text-sm font-semibold text-red-800 hover:bg-red-50">
      {ten}
    </button>
  }

  return (
    <form onSubmit={xacNhan} className="space-y-3 rounded-xl border border-red-200 bg-red-50 p-3 text-sm">
      <p className="text-red-900">{canhBao}</p>
      <label htmlFor={idLyDo} className="block font-semibold">
        Lý do hủy
        <textarea id={idLyDo} rows={2} required autoFocus disabled={dangHuy} value={lyDo}
          onChange={(event) => setLyDo(event.target.value)}
          className="mt-1 block w-full rounded-lg border border-slate-300 bg-white p-2" />
      </label>
      {loi && <p role="alert" className="text-red-800">{loi}</p>}
      <div className="flex flex-wrap gap-2">
        <button type="button" disabled={dangHuy} onClick={() => { setMo(false); setLoi('') }}
          className="rounded-lg border border-slate-200 bg-white px-3 py-2 font-semibold">Quay lại</button>
        <button type="submit" disabled={dangHuy || !lyDo.trim()}
          className="rounded-lg bg-red-700 px-3 py-2 font-semibold text-white disabled:opacity-60">
          {dangHuy ? 'Đang hủy…' : `Xác nhận ${ten.toLocaleLowerCase('vi')}`}
        </button>
      </div>
    </form>
  )
}
